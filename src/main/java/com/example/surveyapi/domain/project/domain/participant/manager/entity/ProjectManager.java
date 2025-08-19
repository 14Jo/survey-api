package com.example.surveyapi.domain.project.domain.participant.manager.entity;

import com.example.surveyapi.domain.project.domain.participant.ProjectParticipant;
import com.example.surveyapi.domain.project.domain.participant.manager.enums.ManagerRole;
import com.example.surveyapi.domain.project.domain.project.entity.Project;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "project_managers")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProjectManager extends ProjectParticipant {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private ManagerRole role;

	public static ProjectManager create(Project project, Long userId) {
		ProjectManager projectManager = new ProjectManager(project, userId);
		projectManager.role = ManagerRole.READ;

		return projectManager;
	}

	public static ProjectManager createOwner(Project project, Long userId) {
		ProjectManager projectManager = new ProjectManager(project, userId);
		projectManager.role = ManagerRole.OWNER;

		return projectManager;
	}

	private ProjectManager(Project project, Long userId) {
		super(project, userId);
	}

	public void updateRole(ManagerRole role) {
		this.role = role;
	}

	public boolean isOwner() {
		return this.role == ManagerRole.OWNER;
	}
}
