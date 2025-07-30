package com.example.surveyapi.domain.project.application.dto.response;

import java.time.LocalDateTime;

import com.example.surveyapi.domain.project.domain.dto.ProjectSearchResult;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProjectSearchInfoResponse {
	private Long projectId;
	private String name;
	private String description;
	private Long ownerId;
	private String state;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	public static ProjectSearchInfoResponse from(ProjectSearchResult result) {
		ProjectSearchInfoResponse response = new ProjectSearchInfoResponse();
		response.projectId = result.getProjectId();
		response.name = result.getName();
		response.description = result.getDescription();
		response.ownerId = result.getOwnerId();
		response.state = result.getState();
		response.createdAt = result.getCreatedAt();
		response.updatedAt = result.getUpdatedAt();

		return response;
	}
}