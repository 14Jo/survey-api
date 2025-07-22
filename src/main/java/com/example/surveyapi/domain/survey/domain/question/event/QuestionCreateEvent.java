package com.example.surveyapi.domain.survey.domain.question.event;

import java.util.List;

import com.example.surveyapi.domain.survey.application.request.CreateChoiceRequest;

import lombok.Getter;

@Getter
public class QuestionCreateEvent {

	private final Long questionId;
	private final List<CreateChoiceRequest> choiceList;

	public QuestionCreateEvent(Long questionId, List<CreateChoiceRequest> choiceList) {
		this.questionId = questionId;
		this.choiceList = choiceList;
	}
}
