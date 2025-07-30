package com.example.surveyapi.global.event;

import com.example.surveyapi.domain.survey.domain.survey.enums.SurveyStatus;
import com.example.surveyapi.global.model.SurveyEvent;

import lombok.Getter;

@Getter
public class SurveyActivateEvent implements SurveyEvent {

	private Long surveyId;
	private SurveyStatus surveyStatus;

	public SurveyActivateEvent(Long surveyId, SurveyStatus surveyStatus) {
		this.surveyId = surveyId;
		this.surveyStatus = surveyStatus;
	}
}
