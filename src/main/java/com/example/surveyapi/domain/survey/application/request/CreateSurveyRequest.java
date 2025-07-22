package com.example.surveyapi.domain.survey.application.request;

import java.time.LocalDateTime;
import java.util.List;

import com.example.surveyapi.domain.survey.domain.survey.enums.SurveyType;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class CreateSurveyRequest {

	@NotBlank
	private String title;

	private String description;

	@NotNull
	private LocalDateTime startDate;

	@NotNull
	private LocalDateTime endDate;

	@NotNull
	private SurveyType surveyType;

	private boolean isAllowMultiple = false;
	private boolean isAllowResponseUpdate = false;
	private boolean isAnonymous = false;

	private List<CreateQuestionRequest> questions;

	@AssertTrue(message = "시작 일은 종료 일보다 이전 이어야 합니다.")
	public boolean isStartBeforeEnd() {
		return startDate != null && endDate != null && startDate.isBefore(endDate);
	}

	@AssertTrue(message = "종료 일은 현재 보다 이후 여야 합니다.")
	public boolean isEndAfterNow() {
		return endDate != null && endDate.isAfter(LocalDateTime.now());
	}

}
