package com.example.surveyapi.domain.participation.application.client;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import com.example.surveyapi.domain.participation.application.client.enums.SurveyApiQuestionType;
import com.example.surveyapi.domain.participation.application.client.enums.SurveyApiStatus;

import lombok.Getter;

@Getter
public class SurveyDetailDto implements Serializable {

	private Long surveyId;
	private SurveyApiStatus status;
	private Duration duration;
	private Option option;
	private List<QuestionValidationInfo> questions;

	@Getter
	public static class Duration implements Serializable {
		private LocalDateTime endDate;
	}

	@Getter
	public static class Option implements Serializable {
		private boolean allowResponseUpdate;
	}

	@Getter
	public static class QuestionValidationInfo implements Serializable {
		private Long questionId;
		private boolean isRequired;
		private SurveyApiQuestionType questionType;
	}
}
