package com.example.surveyapi.domain.project.domain.participant;

import com.example.surveyapi.domain.project.domain.project.entity.Project;
import com.example.surveyapi.global.model.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@MappedSuperclass
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class ProjectParticipant extends BaseEntity {

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "project_id", nullable = false)
	protected Project project;

	@Column(nullable = false)
	protected Long userId;

	protected ProjectParticipant(Project project, Long userId) {
		this.project = project;
		this.userId = userId;
	}

	public boolean isSameUser(Long userId) {
		return this.userId.equals(userId);
	}
}