package com.example.surveyapi.project.application.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class UpdateProjectOwnerRequest {
	@NotNull(message = "위임할 회원 ID를 입력해주세요")
	private Long newOwnerId;
}