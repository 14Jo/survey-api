package com.example.surveyapi.domain.project.application.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class CreateManagerResponse {
	private Long managerId;

	public static CreateManagerResponse from(Long managerId) {
		return new CreateManagerResponse(managerId);
	}
}