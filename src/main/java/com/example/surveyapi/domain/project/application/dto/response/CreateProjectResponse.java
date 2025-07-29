package com.example.surveyapi.domain.project.application.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class CreateProjectResponse {
	private Long projectId;
	private int maxMembers;

	public static CreateProjectResponse of(Long projectId, int maxMembers) {
		return new CreateProjectResponse(projectId, maxMembers);
	}
}