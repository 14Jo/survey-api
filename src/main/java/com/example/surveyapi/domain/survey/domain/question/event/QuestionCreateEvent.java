package com.example.surveyapi.domain.survey.domain.question.event;

import java.util.List;

import com.example.surveyapi.domain.survey.application.request.CreateChoiceRequest;

import lombok.Getter;

@Getter
public class QuestionCreateEvent {

	private Long questionId;
	private List<CreateChoiceRequest> choiceList;

	public QuestionCreateEvent(Long questionId, List<CreateChoiceRequest> choiceList) {
		this.questionId = questionId;
		this.choiceList = choiceList;
	}
}
