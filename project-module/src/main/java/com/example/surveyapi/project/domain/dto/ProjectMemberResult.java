package com.example.surveyapi.project.domain.dto;

import java.time.LocalDateTime;

import com.querydsl.core.annotations.QueryProjection;

import lombok.Getter;

@Getter
public class ProjectMemberResult {
	private final Long projectId;
	private final String name;
	private final String description;
	private final Long ownerId;
	private final LocalDateTime periodStart;
	private final LocalDateTime periodEnd;
	private final String state;
	private final int currentMemberCount;
	private final int maxMembers;
	private final LocalDateTime createdAt;
	private final LocalDateTime updatedAt;

	@QueryProjection
	public ProjectMemberResult(Long projectId, String name, String description, Long ownerId, LocalDateTime periodStart,
		LocalDateTime periodEnd, String state, int currentMemberCount, int maxMembers,
		LocalDateTime createdAt, LocalDateTime updatedAt) {
		this.projectId = projectId;
		this.name = name;
		this.description = description;
		this.ownerId = ownerId;
		this.periodStart = periodStart;
		this.periodEnd = periodEnd;
		this.state = state;
		this.currentMemberCount = currentMemberCount;
		this.maxMembers = maxMembers;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}
}
