package com.example.surveyapi.domain.survey.domain.survey.event;

import java.util.List;

import com.example.surveyapi.domain.survey.application.request.CreateQuestionRequest;

import lombok.Getter;

@Getter
public class SurveyCreatedEvent {

	private final Long surveyId;
	private final List<CreateQuestionRequest> questions;

	public SurveyCreatedEvent(Long surveyId, List<CreateQuestionRequest> questions) {
		this.surveyId = surveyId;
		this.questions = questions;
	}
}
