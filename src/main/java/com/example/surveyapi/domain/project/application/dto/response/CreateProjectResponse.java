package com.example.surveyapi.domain.project.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CreateProjectResponse {
	private Long projectId;

	public static CreateProjectResponse toDto(Long projectId) {
		return new CreateProjectResponse(projectId);
	}
}