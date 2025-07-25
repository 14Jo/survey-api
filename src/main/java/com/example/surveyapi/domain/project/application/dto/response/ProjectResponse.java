package com.example.surveyapi.domain.project.application.dto.response;

import java.time.LocalDateTime;

import com.example.surveyapi.domain.project.domain.dto.ProjectResult;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProjectResponse {
	private Long projectId;
	private String name;
	private String description;
	private Long ownerId;
	private String myRole;
	private LocalDateTime periodStart;
	private LocalDateTime periodEnd;
	private String state;
	private int managersCount;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	public static ProjectResponse from(ProjectResult projectResult) {
		ProjectResponse response = new ProjectResponse();
		response.projectId = projectResult.getProjectId();
		response.name = projectResult.getName();
		response.description = projectResult.getDescription();
		response.ownerId = projectResult.getOwnerId();
		response.myRole = projectResult.getMyRole();
		response.periodStart = projectResult.getPeriodStart();
		response.periodEnd = projectResult.getPeriodEnd();
		response.state = projectResult.getState();
		response.managersCount = projectResult.getManagersCount();
		response.createdAt = projectResult.getCreatedAt();
		response.updatedAt = projectResult.getUpdatedAt();

		return response;
	}
}