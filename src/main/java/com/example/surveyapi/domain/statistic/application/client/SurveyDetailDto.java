package com.example.surveyapi.domain.statistic.application.client;

import java.util.List;

public record SurveyDetailDto (
	Long surveyId,
	String title,
	List<QuestionInfo> questions
) {
	public record QuestionInfo (
		Long questionId,
		String content,
		String questionType,
		int displayOrder,
		List<ChoiceInfo> choices
	) {}

	public record ChoiceInfo (
		Long choiceId,
		String content,
		int displayOrder
	) {}
}