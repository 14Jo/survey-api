package com.example.surveyapi.domain.statistic.application.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.Getter;

@Getter
public class StatisticDetailResponse {
	private final Long surveyId;
	private final List<QuestionStat> baseStats;
	private final LocalDateTime generatedAt;

	public StatisticDetailResponse(Long surveyId, List<QuestionStat> baseStats) {
		this.surveyId = surveyId;
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

		public QuestionStat(Long questionId, String questionContent, String choiceType, int responseCount, List<ChoiceStat> choiceStats) {
			this.questionId = questionId;
			this.questionContent = questionContent;
			this.choiceType = choiceType;
			this.responseCount = responseCount;
			this.choiceStats = choiceStats;
		}
	}

	// --- Polymorphic (다형적) DTO를 위한 설정 ---
	@JsonTypeInfo(use = JsonTypeInfo.Id.NONE) // 타입을 위한 별도 필드 없이 내용으로 구분
	@JsonSubTypes({
		@JsonSubTypes.Type(value = SelectChoiceStat.class),
		@JsonSubTypes.Type(value = TextStat.class)
	})
	public interface ChoiceStat {}

	@Getter
	@JsonInclude(JsonInclude.Include.NON_NULL) // null 필드는 JSON에서 제외
	public static class SelectChoiceStat implements ChoiceStat {
		private final Long choiceId;
		private final String choiceContent;
		private final Integer choiceCount;
		private final String choiceRatio;

		public SelectChoiceStat(Long choiceId, String choiceContent, Integer choiceCount, String choiceRatio) {
			this.choiceId = choiceId;
			this.choiceContent = choiceContent;
			this.choiceCount = choiceCount;
			this.choiceRatio = choiceRatio;
		}
	}

	@Getter
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class TextStat implements ChoiceStat {
		private final String text;

		public TextStat(String text) {
			this.text = text;
		}
	}
}