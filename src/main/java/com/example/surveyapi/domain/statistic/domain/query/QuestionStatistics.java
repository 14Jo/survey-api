package com.example.surveyapi.domain.statistic.domain.query;

import java.util.List;

import lombok.Getter;

@Getter
public class QuestionStatistics {
	private final Long questionId;
	private final String questionContent;
	private final String choiceType;  // "선택형", "텍스트", "다중 선택형"
	private final int responseCount;
	private final List<ChoiceStatistics> choiceStats;

	public QuestionStatistics(Long questionId, String questionContent, String choiceType,
		int responseCount, List<ChoiceStatistics> choiceStats) {
		this.questionId = questionId;
		this.questionContent = questionContent;
		this.choiceType = choiceType;
		this.responseCount = responseCount;
		this.choiceStats = choiceStats;
	}
}
