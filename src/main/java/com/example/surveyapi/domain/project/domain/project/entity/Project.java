package com.example.surveyapi.domain.project.domain.project.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.example.surveyapi.domain.project.domain.participant.manager.entity.ProjectManager;
import com.example.surveyapi.domain.project.domain.participant.manager.enums.ManagerRole;
import com.example.surveyapi.domain.project.domain.participant.member.entity.ProjectMember;
import com.example.surveyapi.domain.project.domain.project.enums.ProjectState;
import com.example.surveyapi.domain.project.domain.project.event.ProjectDeletedDomainEvent;
import com.example.surveyapi.domain.project.domain.project.event.ProjectManagerAddedDomainEvent;
import com.example.surveyapi.domain.project.domain.project.event.ProjectMemberAddedDomainEvent;
import com.example.surveyapi.domain.project.domain.project.event.ProjectStateChangedDomainEvent;
import com.example.surveyapi.domain.project.domain.project.vo.ProjectPeriod;
import com.example.surveyapi.global.exception.CustomErrorCode;
import com.example.surveyapi.global.exception.CustomException;
import com.example.surveyapi.global.model.AbstractRoot;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 애그리거트 루트
 */
@Entity
@Table(name = "projects")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Project extends AbstractRoot<Project> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Version
	private Long version;

	@Column(nullable = false, unique = true)
	private String name;

	@Column(columnDefinition = "TEXT", nullable = false)
	private String description;

	@Column(nullable = false)
	private Long ownerId;

	@Embedded
	private ProjectPeriod period;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private ProjectState state = ProjectState.PENDING;

	@Column(nullable = false)
	private int maxMembers;

	@OneToMany(mappedBy = "project", cascade = {CascadeType.MERGE, CascadeType.PERSIST})
	private List<ProjectManager> projectManagers = new ArrayList<>();

	@OneToMany(mappedBy = "project", cascade = {CascadeType.MERGE, CascadeType.PERSIST})
	private List<ProjectMember> projectMembers = new ArrayList<>();

	public static Project create(String name, String description, Long ownerId, int maxMembers,
		LocalDateTime periodStart, LocalDateTime periodEnd) {
		ProjectPeriod period = ProjectPeriod.of(periodStart, periodEnd);

		Project project = new Project();
		project.name = name;
		project.description = description;
		project.ownerId = ownerId;
		project.period = period;
		project.maxMembers = maxMembers;
		// 프로젝트 생성자는 소유자로 등록
		project.projectManagers.add(ProjectManager.createOwner(project, ownerId));

		return project;
	}

	public void updateProject(String newName, String newDescription, LocalDateTime newPeriodStart,
		LocalDateTime newPeriodEnd) {
		if (newPeriodStart != null || newPeriodEnd != null) {
			LocalDateTime start = Objects.requireNonNullElse(newPeriodStart, this.period.getPeriodStart());
			LocalDateTime end = Objects.requireNonNullElse(newPeriodEnd, this.period.getPeriodEnd());
			this.period = ProjectPeriod.of(start, end);
		}
		if (newName != null && !newName.trim().isEmpty()) {
			this.name = newName;
		}
		if (newDescription != null && !newDescription.trim().isEmpty()) {
			this.description = newDescription;
		}
	}

	public void updateState(ProjectState newState) {
		// PENDING -> IN_PROGRESS만 허용 periodStart를 now로 세팅
		if (this.state == ProjectState.PENDING) {
			if (newState != ProjectState.IN_PROGRESS) {
				throw new CustomException(CustomErrorCode.INVALID_STATE_TRANSITION);
			}
			this.period = ProjectPeriod.of(LocalDateTime.now(), this.period.getPeriodEnd());
		}
		// IN_PROGRESS -> CLOSED만 허용 periodEnd를 now로 세팅
		if (this.state == ProjectState.IN_PROGRESS) {
			if (newState != ProjectState.CLOSED) {
				throw new CustomException(CustomErrorCode.INVALID_STATE_TRANSITION);
			}
			this.period = ProjectPeriod.of(this.period.getPeriodStart(), LocalDateTime.now());
		}

		this.state = newState;
		registerEvent(new ProjectStateChangedDomainEvent(this.id, newState));
	}

	public void updateOwner(Long currentUserId, Long newOwnerId) {
		checkOwner(currentUserId);

		ProjectManager previousOwner = findManagerByUserId(this.ownerId);
		ProjectManager newOwner = findManagerByUserId(newOwnerId);

		if (previousOwner.isSameUser(newOwnerId)) {
			throw new CustomException(CustomErrorCode.CANNOT_TRANSFER_TO_SELF);
		}

		newOwner.updateRole(ManagerRole.OWNER);
		previousOwner.updateRole(ManagerRole.READ);
		this.ownerId = newOwnerId;
	}

	public void softDelete(Long currentUserId) {
		checkOwner(currentUserId);
		this.state = ProjectState.CLOSED;

		// 기존 프로젝트 담당자 같이 삭제
		if (this.projectManagers != null) {
			this.projectManagers.forEach(ProjectManager::delete);
		}

		// 기존 프로젝트 참여자 같이 삭제
		if (this.projectMembers != null) {
			this.projectMembers.forEach(ProjectMember::delete);
		}

		this.delete();
		registerEvent(new ProjectDeletedDomainEvent(this.id, this.name, currentUserId));
	}

	public void addManager(Long currentUserId) {
		// 중복 가입 체크
		boolean exists = this.projectManagers.stream()
			.anyMatch(manager -> manager.isSameUser(currentUserId) && !manager.getIsDeleted());
		if (exists) {
			throw new CustomException(CustomErrorCode.ALREADY_REGISTERED_MANAGER);
		}

		ProjectManager newProjectManager = ProjectManager.create(this, currentUserId);
		this.projectManagers.add(newProjectManager);

		registerEvent(
			new ProjectManagerAddedDomainEvent(currentUserId, this.period.getPeriodEnd(), this.ownerId, this.id));
	}

	public void updateManagerRole(Long currentUserId, Long managerId, ManagerRole newRole) {
		checkOwner(currentUserId);
		ProjectManager projectManager = findManagerById(managerId);

		// 본인 OWNER 권한 변경 불가
		if (projectManager.isSameUser(currentUserId)) {
			throw new CustomException(CustomErrorCode.CANNOT_CHANGE_OWNER_ROLE);
		}

		if (newRole == ManagerRole.OWNER) {
			throw new CustomException(CustomErrorCode.CANNOT_CHANGE_OWNER_ROLE);
		}

		// 현재 소유자인 경우 권한 변경 불가
		if (projectManager.isOwner()) {
			throw new CustomException(CustomErrorCode.CANNOT_CHANGE_OWNER_ROLE);
		}

		projectManager.updateRole(newRole);
	}

	public void deleteManager(Long currentUserId, Long managerId) {
		checkOwner(currentUserId);
		ProjectManager projectManager = findManagerById(managerId);

		if (projectManager.isSameUser(currentUserId)) {
			throw new CustomException(CustomErrorCode.CANNOT_DELETE_SELF_OWNER);
		}

		projectManager.delete();
	}

	public void removeManager(Long currentUserId) {
		ProjectManager manager = findManagerByUserId(currentUserId);
		manager.delete();
	}

	// Manager 조회 헬퍼 메소드
	public ProjectManager findManagerByUserId(Long userId) {

		return this.projectManagers.stream()
			.filter(manager -> manager.isSameUser(userId) && !manager.getIsDeleted())
			.findFirst()
			.orElseThrow(() -> new CustomException(CustomErrorCode.NOT_FOUND_MANAGER));
	}

	public ProjectManager findManagerById(Long managerId) {

		return this.projectManagers.stream()
			.filter(manager -> Objects.equals(manager.getId(), managerId))
			.findFirst()
			.orElseThrow(() -> new CustomException(CustomErrorCode.NOT_FOUND_MANAGER));
	}

	public void addMember(Long currentUserId) {
		// 중복 가입 체크
		boolean exists = this.projectMembers.stream()
			.anyMatch(
				member -> member.isSameUser(currentUserId) && !member.getIsDeleted());
		if (exists) {
			throw new CustomException(CustomErrorCode.ALREADY_REGISTERED_MEMBER);
		}

		// 최대 인원수 체크
		if (getCurrentMemberCount() >= this.maxMembers) {
			throw new CustomException(CustomErrorCode.PROJECT_MEMBER_LIMIT_EXCEEDED);
		}

		this.projectMembers.add(ProjectMember.create(this, currentUserId));

		registerEvent(
			new ProjectMemberAddedDomainEvent(currentUserId, this.period.getPeriodEnd(), this.ownerId, this.id));
	}

	public void removeMember(Long currentUserId) {
		ProjectMember member = findMemberByUserId(currentUserId);
		member.delete();
	}

	// Member 조회 헬퍼 메소드
	private ProjectMember findMemberByUserId(Long userId) {
		return this.projectMembers.stream()
			.filter(member -> member.isSameUser(userId))
			.findFirst()
			.orElseThrow(() -> new CustomException(CustomErrorCode.NOT_FOUND_MEMBER));
	}

	public int getCurrentMemberCount() {

		return (int)this.projectMembers.stream()
			.filter(member -> !member.getIsDeleted())
			.count();
	}

	// 권한 검증 헬퍼 메소드
	private void checkOwner(Long currentUserId) {
		if (!this.ownerId.equals(currentUserId)) {
			throw new CustomException(CustomErrorCode.ACCESS_DENIED);
		}
	}
}