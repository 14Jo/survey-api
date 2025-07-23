package com.example.surveyapi.domain.survey.domain.survey.event;

import java.util.List;

import com.example.surveyapi.domain.survey.domain.survey.vo.QuestionCreationInfo;

import lombok.Getter;
import lombok.Setter;

@Getter
public class SurveyCreatedEvent {

	@Setter
	private Long surveyId;
	private final List<QuestionCreationInfo> questions;

	public SurveyCreatedEvent(Long surveyId, List<QuestionCreationInfo> questions) {
		this.surveyId = surveyId;
		this.questions = questions;
	}
}
