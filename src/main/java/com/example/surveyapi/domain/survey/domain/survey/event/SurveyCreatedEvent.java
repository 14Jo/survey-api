package com.example.surveyapi.domain.survey.domain.survey.event;

import java.util.List;

import com.example.surveyapi.domain.survey.domain.request.CreateQuestionRequest;

import lombok.Getter;

@Getter
public class SurveyCreatedEvent {

	private Long surveyId;
	private List<CreateQuestionRequest> questions;

	public SurveyCreatedEvent(Long surveyId, List<CreateQuestionRequest> questions) {
		this.surveyId = surveyId;
		this.questions = questions;
	}
}
