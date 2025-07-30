package com.example.surveyapi.domain.project.application.dto.response;

import java.time.LocalDateTime;

import com.example.surveyapi.domain.project.domain.dto.ProjectMemberResult;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProjectMemberInfoResponse {
	private Long projectId;
	private String name;
	private String description;
	private Long ownerId;
	private LocalDateTime periodStart;
	private LocalDateTime periodEnd;
	private String state;
	private int managersCount;
	private int currentMemberCount;
	private int maxMembers;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	public static ProjectMemberInfoResponse from(ProjectMemberResult projectMemberResult) {
		ProjectMemberInfoResponse response = new ProjectMemberInfoResponse();
		response.projectId = projectMemberResult.getProjectId();
		response.name = projectMemberResult.getName();
		response.description = projectMemberResult.getDescription();
		response.ownerId = projectMemberResult.getOwnerId();
		response.periodStart = projectMemberResult.getPeriodStart();
		response.periodEnd = projectMemberResult.getPeriodEnd();
		response.state = projectMemberResult.getState();
		response.managersCount = projectMemberResult.getManagersCount();
		response.currentMemberCount = projectMemberResult.getCurrentMemberCount();
		response.maxMembers = projectMemberResult.getMaxMembers();
		response.createdAt = projectMemberResult.getCreatedAt();
		response.updatedAt = projectMemberResult.getUpdatedAt();

		return response;
	}
}

