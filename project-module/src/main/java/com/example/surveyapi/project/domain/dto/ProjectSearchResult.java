package com.example.surveyapi.project.domain.dto;

import java.time.LocalDateTime;

import com.querydsl.core.annotations.QueryProjection;

import lombok.Getter;

@Getter
public class ProjectSearchResult {
	private final Long projectId;
	private final String name;
	private final String description;
	private final Long ownerId;
	private final String state;
	private final LocalDateTime createdAt;
	private final LocalDateTime updatedAt;

	@QueryProjection
	public ProjectSearchResult(Long projectId, String name, String description, Long ownerId,
		String state, LocalDateTime createdAt, LocalDateTime updatedAt) {
		this.projectId = projectId;
		this.name = name;
		this.description = description;
		this.ownerId = ownerId;
		this.state = state;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}
}