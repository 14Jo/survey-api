package com.example.surveyapi.domain.survey.domain.survey.event;

import lombok.Getter;

@Getter
public class SurveyDeletedEvent {

	private Long surveyId;

	public SurveyDeletedEvent(Long surveyId) {
		this.surveyId = surveyId;
	}
}
