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

	public static CreateProjectResponse from(Long projectId) {
		return new CreateProjectResponse(projectId);
	}
}