package com.example.surveyapi.domain.project.domain.project;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.example.surveyapi.domain.project.domain.manager.Manager;
import com.example.surveyapi.domain.project.domain.project.enums.ProjectState;
import com.example.surveyapi.domain.project.domain.project.vo.ProjectPeriod;
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
}
