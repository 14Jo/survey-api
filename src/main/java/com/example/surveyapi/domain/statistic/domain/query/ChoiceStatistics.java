package com.example.surveyapi.domain.statistic.domain.query;

import lombok.Getter;

@Getter
public class ChoiceStatistics {
	private final Long choiceId;
	private final String choiceContent;
	private final Integer choiceCount;
	private final String choiceRatio;
	private final String text; // 서술형 응답일 경우만 사용

	private ChoiceStatistics(Long choiceId, String choiceContent,
		Integer choiceCount, String choiceRatio, String text) {
		this.choiceId = choiceId;
		this.choiceContent = choiceContent;
		this.choiceCount = choiceCount;
		this.choiceRatio = choiceRatio;
		this.text = text;
	}

	// 선택형 생성자
	public static ChoiceStatistics of(Long choiceId, String choiceContent,
		Integer choiceCount, String choiceRatio) {
		return new ChoiceStatistics(choiceId, choiceContent, choiceCount, choiceRatio, null);
	}

	// 서술형 생성자
	public static ChoiceStatistics text(String text) {
		return new ChoiceStatistics(null, null, null, null, text);
	}
}
