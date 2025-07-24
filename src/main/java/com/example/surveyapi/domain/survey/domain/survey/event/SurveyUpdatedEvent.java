package com.example.surveyapi.domain.survey.domain.survey.event;

import java.util.List;

import com.example.surveyapi.domain.survey.domain.survey.vo.QuestionInfo;

import lombok.Getter;

@Getter
public class SurveyUpdatedEvent {

	private Long surveyId;
	private List<QuestionInfo> questions;

	public SurveyUpdatedEvent(Long surveyId, List<QuestionInfo> questions) {
		this.surveyId = surveyId;
		this.questions = questions;
	}
}
