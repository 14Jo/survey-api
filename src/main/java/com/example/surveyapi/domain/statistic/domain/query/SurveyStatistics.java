package com.example.surveyapi.domain.statistic.domain.query;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Getter;

@Getter
public class SurveyStatistics {
	private final Long surveyId;
	private final String surveyTitle;
	private final int totalResponseCount;
	private final List<QuestionStatistics> questionStats;
	private final LocalDateTime generatedAt;

	public SurveyStatistics(Long surveyId, String surveyTitle,
		int totalResponseCount, List<QuestionStatistics> questionStats,
		LocalDateTime generatedAt) {
		this.surveyId = surveyId;
		this.surveyTitle = surveyTitle;
		this.totalResponseCount = totalResponseCount;
		this.questionStats = questionStats;
		this.generatedAt = generatedAt;
	}
}
