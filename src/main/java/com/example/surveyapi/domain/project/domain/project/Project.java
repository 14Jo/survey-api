package com.example.surveyapi.domain.project.domain.project;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.util.StringUtils;

import com.example.surveyapi.domain.project.domain.manager.Manager;
import com.example.surveyapi.domain.project.domain.manager.enums.ManagerRole;
import com.example.surveyapi.domain.project.domain.project.enums.ProjectState;
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

	@OneToMany(mappedBy = "project", cascade = CascadeType.PERSIST, orphanRemoval = true)
	private List<Manager> managers = new ArrayList<>();

	public static Project create(String name, String description, Long ownerId, LocalDateTime periodStart,
		LocalDateTime periodEnd) {
		Project project = new Project();
		ProjectPeriod period = ProjectPeriod.toPeriod(periodStart, periodEnd);
		project.name = name;
		project.description = description;
		project.ownerId = ownerId;
		project.period = period;
		project.managers.add(Manager.createOwner(project, ownerId));
		return project;
	}

	public void updateProject(String newName, String newDescription, LocalDateTime newPeriodStart,
		LocalDateTime newPeriodEnd) {
		if (newPeriodStart != null || newPeriodEnd != null) {
			LocalDateTime start = Objects.requireNonNullElse(newPeriodStart, this.period.getPeriodStart());
			LocalDateTime end = Objects.requireNonNullElse(newPeriodEnd, this.period.getPeriodEnd());
			this.period = ProjectPeriod.toPeriod(start, end);
		}
		if (StringUtils.hasText(newName)) {
			this.name = newName;
		}
		if (StringUtils.hasText(newDescription)) {
			this.description = newDescription;
		}
	}

	public void updateState(ProjectState newState) {
		// 이미 CLOSED 프로젝트는 상태 변경 불가
		if (this.state == ProjectState.CLOSED) {
			throw new CustomException(CustomErrorCode.INVALID_PROJECT_STATE);
		}

		// PENDING -> IN_PROGRESS만 허용 periodStart를 now로 세팅
		if (this.state == ProjectState.PENDING) {
			if (newState != ProjectState.IN_PROGRESS) {
				throw new CustomException(CustomErrorCode.INVALID_STATE_TRANSITION);
			}
			this.period = ProjectPeriod.toPeriod(LocalDateTime.now(), this.period.getPeriodEnd());
		}
		// IN_PROGRESS -> CLOSED만 허용 periodEnd를 now로 세팅
		if (this.state == ProjectState.IN_PROGRESS) {
			if (newState != ProjectState.CLOSED) {
				throw new CustomException(CustomErrorCode.INVALID_STATE_TRANSITION);
			}
			this.period = ProjectPeriod.toPeriod(this.period.getPeriodStart(), LocalDateTime.now());
		}

		this.state = newState;
	}

	public void updateOwner(Long currentUserId, Long newOwnerId) {
		checkOwner(currentUserId);
		// 소유자 위임
		Manager newOwner = findManagerByUserId(newOwnerId);
		newOwner.updateRole(ManagerRole.OWNER);

		// 기존 소유자는 READ 권한으로 변경
		Manager previousOwner = findManagerByUserId(this.ownerId);
		previousOwner.updateRole(ManagerRole.READ);
	}

	public void softDelete(Long currentUserId) {
		checkOwner(currentUserId);
		this.state = ProjectState.CLOSED;

		// 기존 프로젝트 담당자 같이 삭제
		if (this.managers != null) {
			this.managers.forEach(Manager::delete);
		}

		this.delete();
	}

	private void checkOwner(Long currentUserId) {
		if (!this.ownerId.equals(currentUserId)) {
			throw new CustomException(CustomErrorCode.OWNER_ONLY);
		}
	}

	private Manager findManagerByUserId(Long userId) {
		return this.managers.stream()
			.filter(manager -> manager.getUserId().equals(userId))
			.findFirst()
			.orElseThrow(() -> new CustomException(CustomErrorCode.NOT_FOUND_MANAGER));
	}
}