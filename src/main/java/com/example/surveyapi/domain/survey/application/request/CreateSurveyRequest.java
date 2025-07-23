package com.example.surveyapi.domain.survey.application.request;

import java.time.LocalDateTime;
import java.util.List;

import com.example.surveyapi.domain.survey.domain.survey.enums.SurveyType;
import com.example.surveyapi.domain.survey.domain.survey.vo.QuestionInfo;
import com.example.surveyapi.domain.survey.domain.survey.vo.SurveyDuration;
import com.example.surveyapi.domain.survey.domain.survey.vo.SurveyOption;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateSurveyRequest {

	@NotBlank
	private String title;

	private String description;

	@NotNull
	private SurveyType surveyType;

	@NotNull
	private SurveyDuration surveyDuration;

	@NotNull
	private SurveyOption surveyOption;

	private List<QuestionInfo> questions;

	@AssertTrue(message = "시작 일과 종료를 입력 해야 합니다.")
	public boolean isValidDuration() {
		return surveyDuration != null && surveyDuration.getStartDate() != null && surveyDuration.getEndDate() != null;
	}

	@AssertTrue(message = "시작 일은 종료 일보다 이전 이어야 합니다.")
	public boolean isStartBeforeEnd() {
		return isValidDuration() && surveyDuration.getStartDate().isBefore(surveyDuration.getEndDate());
	}

	@AssertTrue(message = "종료 일은 현재 보다 이후 여야 합니다.")
	public boolean isEndAfterNow() {
		return isValidDuration() && surveyDuration.getEndDate().isAfter(LocalDateTime.now());
	}
}
