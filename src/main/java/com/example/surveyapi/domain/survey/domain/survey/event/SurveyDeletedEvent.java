package com.example.surveyapi.domain.survey.domain.survey.event;

import lombok.Getter;

@Getter
public class SurveyDeletedEvent implements DomainEvent {

	private Long surveyId;

	public SurveyDeletedEvent(Long surveyId) {
		this.surveyId = surveyId;
	}
}
