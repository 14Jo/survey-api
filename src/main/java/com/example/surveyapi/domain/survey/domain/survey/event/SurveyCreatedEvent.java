package com.example.surveyapi.domain.survey.domain.survey.event;

import java.util.List;

import com.example.surveyapi.domain.survey.domain.survey.vo.QuestionInfo;

import lombok.Getter;
import lombok.Setter;

@Getter
public class SurveyCreatedEvent {

	@Setter
	private Long surveyId;
	private final List<QuestionInfo> questions;

	public SurveyCreatedEvent(Long surveyId, List<QuestionInfo> questions) {
		this.surveyId = surveyId;
		this.questions = questions;
	}
}
