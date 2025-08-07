package com.example.surveyapi.domain.participation.application.client;

import java.time.LocalDateTime;
import java.util.List;

import com.example.surveyapi.domain.participation.application.client.enums.SurveyApiQuestionType;
import com.example.surveyapi.domain.participation.application.client.enums.SurveyApiStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class SurveyDetailDto {

	private Long surveyId;
	private SurveyApiStatus status;
	private Duration duration;
	private Option option;
	private List<QuestionValidationInfo> questions;

	@AllArgsConstructor
	@Getter
	public static class Duration {
		private LocalDateTime endDate;
	}

	@AllArgsConstructor
	@Getter
	public static class Option {
		private boolean allowResponseUpdate;
	}

	@AllArgsConstructor
	@Getter
	public static class QuestionValidationInfo {
		private Long questionId;
		private boolean isRequired;
		private SurveyApiQuestionType questionType;
	}
}
