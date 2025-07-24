package com.example.surveyapi.domain.project.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CreateManagerResponse {
	private Long managerId;

	public static CreateManagerResponse from(Long managerId) {
		return new CreateManagerResponse(managerId);
	}
}