package com.example.surveyapi.domain.project.application.dto.request;

import com.example.surveyapi.domain.project.domain.group.enums.AgeGroup;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class CreateGroupRequest {

	@NotNull(message = "연령대를 입력해주세요.")
	private AgeGroup ageGroup;
}