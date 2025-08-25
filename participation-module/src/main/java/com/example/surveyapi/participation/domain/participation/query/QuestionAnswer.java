package com.example.surveyapi.participation.domain.participation.query;

import java.util.Map;

import lombok.Getter;

@Getter
public class QuestionAnswer {

	private Long questionId;
	private Map<String, Object> answer;

	public QuestionAnswer(Long questionId, Map<String, Object> answer) {
		this.questionId = questionId;
		this.answer = answer;
	}
}
