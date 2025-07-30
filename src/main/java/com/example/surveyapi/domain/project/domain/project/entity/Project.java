package com.example.surveyapi.domain.project.domain.project.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.example.surveyapi.domain.project.domain.manager.entity.ProjectManager;
import com.example.surveyapi.domain.project.domain.manager.enums.ManagerRole;
import com.example.surveyapi.domain.project.domain.member.entity.ProjectMember;
import com.example.surveyapi.domain.project.domain.project.enums.ProjectState;
import com.example.surveyapi.domain.project.domain.project.event.DomainEvent;
import com.example.surveyapi.domain.project.domain.project.event.ProjectDeletedEvent;
import com.example.surveyapi.domain.project.domain.project.event.ProjectStateChangedEvent;
import com.example.surveyapi.domain.project.domain.project.vo.ProjectPeriod;
import com.example.surveyapi.global.enums.CustomErrorCode;
import com.example.surveyapi.global.exception.CustomException;
import com.example.surveyapi.global.model.BaseEntity;

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
import jakarta.persistence.Transient;
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
public class Project extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

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

	@Column(nullable = false)
	private int currentMemberCount;

	@OneToMany(mappedBy = "project", cascade = {CascadeType.MERGE, CascadeType.PERSIST}, orphanRemoval = true)
	private List<ProjectManager> projectManagers = new ArrayList<>();

	@OneToMany(mappedBy = "project", cascade = {CascadeType.MERGE, CascadeType.PERSIST}, orphanRemoval = true)
	private List<ProjectMember> projectMembers = new ArrayList<>();

	@Transient
	private final List<DomainEvent> domainEvents = new ArrayList<>();

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
		checkNotClosedState();

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
		checkNotClosedState();

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
		registerEvent(new ProjectStateChangedEvent(this.id, newState));
	}

	public void updateOwner(Long currentUserId, Long newOwnerId) {
		checkNotClosedState();
		checkOwner(currentUserId);
		// 소유자 위임
		ProjectManager newOwner = findManagerByUserId(newOwnerId);
		newOwner.updateRole(ManagerRole.OWNER);

		// 기존 소유자는 READ 권한으로 변경
		ProjectManager previousOwner = findManagerByUserId(this.ownerId);
		previousOwner.updateRole(ManagerRole.READ);
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
		registerEvent(new ProjectDeletedEvent(this.id, this.name));
	}

	public void addManager(Long currentUserId, Long userId) {
		checkNotClosedState();
		// 권한 체크 OWNER, WRITE, STAT만 가능
		ManagerRole myRole = findManagerByUserId(currentUserId).getRole();
		if (myRole == ManagerRole.READ) {
			throw new CustomException(CustomErrorCode.ACCESS_DENIED);
		}

		// 이미 담당자로 등록되어있다면 중복 등록 불가
		boolean exists = this.projectManagers.stream()
			.anyMatch(manager -> manager.getUserId().equals(userId) && !manager.getIsDeleted());
		if (exists) {
			throw new CustomException(CustomErrorCode.ALREADY_REGISTERED_MANAGER);
		}

		ProjectManager newProjectManager = ProjectManager.create(this, userId);
		this.projectManagers.add(newProjectManager);
	}

	public void updateManagerRole(Long currentUserId, Long managerId, ManagerRole newRole) {
		checkOwner(currentUserId);
		ProjectManager projectManager = findManagerById(managerId);

		// 본인 OWNER 권한 변경 불가
		if (Objects.equals(currentUserId, projectManager.getUserId())) {
			throw new CustomException(CustomErrorCode.CANNOT_CHANGE_OWNER_ROLE);
		}
		if (newRole == ManagerRole.OWNER) {
			throw new CustomException(CustomErrorCode.CANNOT_CHANGE_OWNER_ROLE);
		}

		projectManager.updateRole(newRole);
	}

	public void deleteManager(Long currentUserId, Long managerId) {
		checkOwner(currentUserId);
		ProjectManager projectManager = findManagerById(managerId);

		if (Objects.equals(projectManager.getUserId(), currentUserId)) {
			throw new CustomException(CustomErrorCode.CANNOT_DELETE_SELF_OWNER);
		}

		projectManager.delete();
	}

	// List<ProjectManager> 조회 메소드
	public ProjectManager findManagerByUserId(Long userId) {
		return this.projectManagers.stream()
			.filter(projectManager -> projectManager.getUserId().equals(userId) && !projectManager.getIsDeleted())
			.findFirst()
			.orElseThrow(() -> new CustomException(CustomErrorCode.NOT_FOUND_MANAGER));
	}

	public ProjectManager findManagerById(Long managerId) {
		return this.projectManagers.stream()
			.filter(projectManager -> Objects.equals(projectManager.getId(), managerId))
			.findFirst()
			.orElseThrow(() -> new CustomException(CustomErrorCode.NOT_FOUND_MANAGER));
	}

	// TODO: 동시성 문제 해결, stream N+1 생각해보기
	public void addMember(Long currentUserId) {
		checkNotClosedState();
		// 중복 가입 체크
		boolean exists = this.projectMembers.stream()
			.anyMatch(projectMember -> projectMember.getUserId().equals(currentUserId) && !projectMember.getIsDeleted());
		if (exists) {
			throw new CustomException(CustomErrorCode.ALREADY_REGISTERED_MEMBER);
		}

		// 최대 인원수 체크
		if (this.currentMemberCount >= this.maxMembers) {
			throw new CustomException(CustomErrorCode.PROJECT_MEMBER_LIMIT_EXCEEDED);
		}

		this.projectMembers.add(ProjectMember.create(this, currentUserId));
		this.currentMemberCount++;
	}

	public void removeMember(Long currentUserId) {
		checkNotClosedState();
		ProjectMember member = this.projectMembers.stream()
			.filter(projectMember -> projectMember.getUserId().equals(currentUserId) && !projectMember.getIsDeleted())
			.findFirst()
			.orElseThrow(() -> new CustomException(CustomErrorCode.NOT_FOUND_MEMBER));

		member.delete();

		this.currentMemberCount--;
	}

	// 소유자 권한 확인
	private void checkOwner(Long currentUserId) {
		if (!this.ownerId.equals(currentUserId)) {
			throw new CustomException(CustomErrorCode.ACCESS_DENIED);
		}
	}

	// 이벤트 등록/ 관리
	public List<DomainEvent> pullDomainEvents() {
		List<DomainEvent> events = new ArrayList<>(domainEvents);
		domainEvents.clear();
		return events;
	}

	private void registerEvent(DomainEvent event) {
		this.domainEvents.add(event);
	}

	private void checkNotClosedState() {
		if (this.state == ProjectState.CLOSED) {
			throw new CustomException(CustomErrorCode.INVALID_PROJECT_STATE);
		}
	}
}