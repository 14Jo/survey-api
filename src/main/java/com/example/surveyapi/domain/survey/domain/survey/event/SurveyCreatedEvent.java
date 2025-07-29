package com.example.surveyapi.domain.survey.domain.survey.event;

import java.util.List;
import java.util.Optional;
import java.util.OptionalLong;

import com.example.surveyapi.domain.survey.domain.survey.vo.QuestionInfo;

import lombok.Getter;
import lombok.Setter;

@Getter
public class SurveyCreatedEvent implements DomainEvent {

	private Optional<Long> surveyId;
	private final List<QuestionInfo> questions;

	public SurveyCreatedEvent(List<QuestionInfo> questions) {
		this.surveyId = Optional.empty();
		this.questions = questions;
	}

	public void setSurveyId(Long surveyId) {
		this.surveyId = Optional.of(surveyId);
	}
}
