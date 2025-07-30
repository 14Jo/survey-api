package com.example.surveyapi.domain.survey.application.request;

import com.example.surveyapi.domain.survey.domain.survey.enums.SurveyType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateSurveyRequest extends SurveyRequest {

	@NotBlank(message = "설문 제목은 필수입니다.")
	private String title;

	@NotNull(message = "설문 타입은 필수입니다.")
	private SurveyType surveyType;

	@NotNull(message = "설문 기간은 필수입니다.")
	private Duration surveyDuration;

	@NotNull(message = "설문 옵션은 필수입니다.")
	private Option surveyOption;
}
