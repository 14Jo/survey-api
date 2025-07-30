package com.example.surveyapi.domain.project.application.dto.response;

import java.time.LocalDateTime;

import com.example.surveyapi.domain.project.domain.project.entity.Project;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProjectInfoResponse {
	private Long projectId;
	private String name;
	private String description;
	private Long ownerId;
	private LocalDateTime periodStart;
	private LocalDateTime periodEnd;
	private String state;
	private int maxMembers;
	private int currentMemberCount;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	public static ProjectInfoResponse from(Project project) {
		ProjectInfoResponse response = new ProjectInfoResponse();
		response.projectId = project.getId();
		response.name = project.getName();
		response.description = project.getDescription();
		response.ownerId = project.getOwnerId();
		response.periodStart = project.getPeriod().getPeriodStart();
		response.periodEnd = project.getPeriod().getPeriodEnd();
		response.state = project.getState().name();
		response.maxMembers = project.getMaxMembers();
		response.currentMemberCount = project.getCurrentMemberCount();
		response.createdAt = project.getCreatedAt();
		response.updatedAt = project.getUpdatedAt();

		return response;
	}
}
