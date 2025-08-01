package com.example.surveyapi.domain.project.domain.manager.entity;

import com.example.surveyapi.domain.project.domain.manager.enums.ManagerRole;
import com.example.surveyapi.domain.project.domain.project.entity.Project;
import com.example.surveyapi.global.model.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "project_managers")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProjectManager extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "project_id", nullable = false)
	private Project project;

	@Column(nullable = false)
	private Long userId;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private ManagerRole role;

	public static ProjectManager create(Project project, Long userId) {
		ProjectManager projectManager = new ProjectManager();
		projectManager.project = project;
		projectManager.userId = userId;
		projectManager.role = ManagerRole.READ;

		return projectManager;
	}

	public static ProjectManager createOwner(Project project, Long userId) {
		ProjectManager projectManager = new ProjectManager();
		projectManager.project = project;
		projectManager.userId = userId;
		projectManager.role = ManagerRole.OWNER;

		return projectManager;
	}

	public void updateRole(ManagerRole role) {
		this.role = role;
	}
}
