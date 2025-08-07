package com.example.surveyapi.domain.project.domain.participant.member.entity;

import com.example.surveyapi.domain.project.domain.participant.ProjectParticipant;
import com.example.surveyapi.domain.project.domain.project.entity.Project;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "project_members")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProjectMember extends ProjectParticipant {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	public static ProjectMember create(Project project, Long userId) {
		return new ProjectMember(project, userId);
	}

	private ProjectMember(Project project, Long userId) {
		super(project, userId);
	}
}
