package com.example.surveyapi.domain.project.application.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class CreateManagerRequest {
	@NotNull(message = "담당자로 등록할 userId를 입력해주세요.")
	private Long userId;
}