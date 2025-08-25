package com.example.surveyapi.statistic.application.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.example.surveyapi.statistic.domain.query.QuestionStatistics;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;

@Getter
public class StatisticBasicResponse {

	private final Long surveyId;
	private final Long totalResponseCount;
	private final List<QuestionStat> baseStats;
	private final LocalDateTime generatedAt;

	public StatisticBasicResponse(Long surveyId, Long count, List<QuestionStat> baseStats) {
		this.surveyId = surveyId;
		this.totalResponseCount = count;
		this.baseStats = baseStats;
		this.generatedAt = LocalDateTime.now();
	}

	@Getter
	public static class QuestionStat {
		private final Long questionId;
		private final String questionContent;
		private final String choiceType;
		private final int responseCount;
		private final List<ChoiceStat> choiceStats;
		private final List<String> texts;

		public QuestionStat(Long questionId, String questionContent, String choiceType,
			int responseCount, List<ChoiceStat> choiceStats, List<String> texts) {
			this.questionId = questionId;
			this.questionContent = questionContent;
			this.choiceType = choiceType;
			this.responseCount = responseCount;
			this.choiceStats = choiceStats;
			this.texts = texts;
		}

		public static QuestionStat from(QuestionStatistics stats) {
			List<ChoiceStat> choiceDtoStats = null;
			if (stats.choices() != null) {
				choiceDtoStats = stats.choices().stream()
					.map(c -> new ChoiceStat(c.choiceId(), c.content(), c.count()))
					.toList();
			}

			return new QuestionStat(
				stats.questionId(),
				stats.content(),
				stats.type(),
				(int) stats.responseCount(),
				choiceDtoStats,
				stats.texts()
			);
		}
	}

	@Getter
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class ChoiceStat {
		private final Long choiceId;
		private final String choiceContent;
		private final Long choiceCount;

		public ChoiceStat(Long choiceId, String choiceContent, Long choiceCount) {
			this.choiceId = choiceId;
			this.choiceContent = choiceContent;
			this.choiceCount = choiceCount;
		}
	}
}