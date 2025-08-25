package com.example.surveyapi.domain.survey.application.dto.request;

import com.example.surveyapi.domain.survey.domain.survey.enums.SurveyType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateSurveyRequest extends SurveyRequest {

	@NotBlank(message = "설문 제목은 필수입니다.")
	@Override
	public String getTitle() {
		return super.getTitle();
	}

	@NotNull(message = "설문 타입은 필수입니다.")
	@Override
	public SurveyType getSurveyType() {
		return super.getSurveyType();
	}

	@NotNull(message = "설문 기간은 필수입니다.")
	@Override
	public Duration getSurveyDuration() {
		return super.getSurveyDuration();
	}

	@NotNull(message = "설문 옵션은 필수입니다.")
	@Override
	public Option getSurveyOption() {
		return super.getSurveyOption();
	}
}
