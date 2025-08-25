package com.example.surveyapi.statistic.domain.statisticdocument.dto;

import java.util.Map;
import java.util.Optional;

import lombok.Getter;

@Getter
public class SurveyMetadata {
	private final Map<Long, QuestionMetadata> questionMap;

	public SurveyMetadata(Map<Long, QuestionMetadata> questionMap) {
		this.questionMap = questionMap;
	}

	public Optional<QuestionMetadata> getQuestion(Long questionId) {
		return Optional.ofNullable(questionMap.get(questionId));
	}

	public record QuestionMetadata(
		String content,
		String questionType,
		Map<Long, String> choiceMap
	) {
		public Optional<String> getChoiceText(Integer choiceId) {
			return Optional.ofNullable(choiceMap.get(choiceId));
		}
	}
}