package com.example.surveyapi.domain.survey.domain.survey.event;

import java.util.List;

import com.example.surveyapi.domain.survey.domain.survey.vo.QuestionInfo;
import com.example.surveyapi.global.model.SurveyEvent;

import lombok.Getter;

@Getter
public class SurveyUpdatedEvent implements SurveyEvent {

	private Long surveyId;
	private List<QuestionInfo> questions;

	public SurveyUpdatedEvent(Long surveyId, List<QuestionInfo> questions) {
		this.surveyId = surveyId;
		this.questions = questions;
	}
}
