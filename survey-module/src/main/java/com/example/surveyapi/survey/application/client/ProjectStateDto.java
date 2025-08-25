package com.example.surveyapi.survey.application.client;

import java.io.Serializable;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProjectStateDto implements Serializable {

	private String state;

	public static ProjectStateDto of(String state) {
		ProjectStateDto dto = new ProjectStateDto();
		dto.state = state;
		return dto;
	}

	public boolean isClosed() {
		return "CLOSED".equals(state);
	}

	public boolean isInProgress() {
		return "IN_PROGRESS".equals(state);
	}

	public boolean isPending() {
		return "PENDING".equals(state);
	}
} 