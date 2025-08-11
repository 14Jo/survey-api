package com.example.surveyapi.domain.statistic.application.client;

import java.util.List;

public record QuestionAnswers(
	Long questionId,
	List<TextAnswer> answers
) {
	public record TextAnswer(
		String textAnswer
	) {}
}
