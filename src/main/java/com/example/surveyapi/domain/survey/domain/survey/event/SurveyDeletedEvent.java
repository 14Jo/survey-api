package com.example.surveyapi.domain.survey.domain.survey.event;

import com.example.surveyapi.global.model.SurveyEvent;

import lombok.Getter;

@Getter
public class SurveyDeletedEvent implements SurveyEvent {

	private Long surveyId;

	public SurveyDeletedEvent(Long surveyId) {
		this.surveyId = surveyId;
	}
}
