package com.example.surveyapi.project.application.dto.response;

import java.time.LocalDateTime;

import com.example.surveyapi.project.domain.dto.ProjectManagerResult;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProjectManagerInfoResponse {
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

	public static ProjectManagerInfoResponse from(ProjectManagerResult projectManagerResult) {
		ProjectManagerInfoResponse response = new ProjectManagerInfoResponse();
		response.projectId = projectManagerResult.getProjectId();
		response.name = projectManagerResult.getName();
		response.description = projectManagerResult.getDescription();
		response.ownerId = projectManagerResult.getOwnerId();
		response.myRole = projectManagerResult.getMyRole();
		response.periodStart = projectManagerResult.getPeriodStart();
		response.periodEnd = projectManagerResult.getPeriodEnd();
		response.state = projectManagerResult.getState();
		response.managersCount = projectManagerResult.getManagersCount();
		response.createdAt = projectManagerResult.getCreatedAt();
		response.updatedAt = projectManagerResult.getUpdatedAt();

		return response;
	}
}