package com.example.surveyapi.project.application.dto.request;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class CreateProjectRequest {

	@NotBlank(message = "이름을 입력해주세요")
	private String name;

	@NotBlank(message = "설명을 입력해주세요")
	private String description;

	@NotNull(message = "시작일을 입력해주세요")
	@Future(message = "시작일은 현재보다 이후여야 합니다.")
	private LocalDateTime periodStart;

	private LocalDateTime periodEnd;

	@Min(value = 1, message = "최대 인원수는 최소 1명 이상이어야 합니다.")
	@Max(value = 500, message = "최대 인원수는 500명을 초과할 수 없습니다.")
	private int maxMembers;
}
