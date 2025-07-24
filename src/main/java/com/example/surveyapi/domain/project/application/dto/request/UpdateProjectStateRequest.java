package com.example.surveyapi.domain.project.application.dto.request;

import com.example.surveyapi.domain.project.domain.project.enums.ProjectState;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class UpdateProjectStateRequest {
	@NotNull(message = "변경할 상태를 입력해주세요")
	private ProjectState state;
}
