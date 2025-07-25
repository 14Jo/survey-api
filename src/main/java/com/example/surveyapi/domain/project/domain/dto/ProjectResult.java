package com.example.surveyapi.domain.project.domain.dto;

import java.time.LocalDateTime;

import com.querydsl.core.annotations.QueryProjection;

import lombok.Getter;

@Getter
public class ProjectResult {
	private final Long projectId;
	private final String name;
	private final String description;
	private final Long ownerId;
	private final String myRole;
	private final LocalDateTime periodStart;
	private final LocalDateTime periodEnd;
	private final String state;
	private final int managersCount;
	private final LocalDateTime createdAt;
	private final LocalDateTime updatedAt;

	@QueryProjection
	public ProjectResult(Long projectId, String name, String description, Long ownerId, String myRole,
		LocalDateTime periodStart, LocalDateTime periodEnd, String state, int managersCount, LocalDateTime createdAt,
		LocalDateTime updatedAt) {
		this.projectId = projectId;
		this.name = name;
		this.description = description;
		this.ownerId = ownerId;
		this.myRole = myRole;
		this.periodStart = periodStart;
		this.periodEnd = periodEnd;
		this.state = state;
		this.managersCount = managersCount;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}
}