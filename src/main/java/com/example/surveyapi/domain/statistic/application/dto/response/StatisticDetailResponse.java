package com.example.surveyapi.domain.statistic.application.dto.response;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.example.surveyapi.domain.statistic.application.client.SurveyDetailDto;
import com.example.surveyapi.domain.statistic.domain.StatisticReport;
import com.example.surveyapi.domain.statistic.domain.model.aggregate.Statistic;
import com.example.surveyapi.domain.statistic.domain.model.enums.AnswerType;
import com.example.surveyapi.global.enums.CustomErrorCode;
import com.example.surveyapi.global.exception.CustomException;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StatisticDetailResponse {
	private Long surveyId;
	private String surveyTitle;
	private int totalResponseCount;
	private LocalDateTime firstResponseAt;
	private LocalDateTime lastResponseAt;

	private List<TemporalStat> temporalResonseList;

	private List<QuestionStat> questionStatList;

	private LocalDateTime generatedAt;

	public static StatisticDetailResponse of(
		StatisticReport statisticReport,
		SurveyDetailDto surveyDetailDto,
		Statistic statistic,
		List<TemporalStat> temporalResonseList,
		List<QuestionStat> questionStatList
	) {
		StatisticDetailResponse detail = new StatisticDetailResponse();
		detail.surveyId = statistic.getSurveyId();
		detail.surveyTitle = surveyDetailDto.title();
		detail.totalResponseCount = statistic.getStats().getTotalResponses();
		detail.firstResponseAt = statisticReport.getFirstResponseAt();
		detail.lastResponseAt = statisticReport.getLastResponseAt();
		detail.temporalResonseList = temporalResonseList;
		detail.questionStatList = questionStatList;
		detail.generatedAt = LocalDateTime.now();
		return detail;
	}

	@Getter
	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class TemporalStat {
		private LocalDateTime timestamp;
		private int count;

		public static List<TemporalStat> toStats(List<Map<String, Object>> temporalMaps) {
			return temporalMaps.stream()
				.map(map -> TemporalStat.of(
					(LocalDateTime) map.get("timestamp"),
					(int) map.get("count")
				))
				.toList();
		}

		public static TemporalStat of(LocalDateTime timestamp, int count) {
			TemporalStat stat = new TemporalStat();
			stat.timestamp = timestamp;
			stat.count = count;
			return stat;
		}
	}

	@Getter
	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class QuestionStat {
		private Long questionId;
		private String questionContent;
		private String choiceType;
		private int responseCount;

		private List<ChoiceStat> choiceStats;

		public static QuestionStat of(
			StatisticReport.QuestionStatsResult questionStat,
			SurveyDetailDto.QuestionInfo questionInfo,
			List<ChoiceStat> stats
		) {

			QuestionStat stat = new QuestionStat();
			stat.questionId = questionInfo.questionId();
			stat.questionContent = questionInfo.content();
			stat.choiceType = ChoiceType.findByKey(questionStat.answerType()).getDescription();
			stat.responseCount = questionStat.totalCount();
			stat.choiceStats = stats;
			return stat;
		}

	}

	public interface ChoiceStat {}

	@Getter
	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class SelectChoiceStat implements ChoiceStat {
		private Long choiceId;
		private String choiceContent;
		private int choiceCount;
		private String choiceRatio;

		public static List<SelectChoiceStat> toStats(
			List<StatisticReport.ChoiceStatsResult> choices,
			List<SurveyDetailDto.ChoiceInfo> choiceInfos
		) {
			Map<Long, String> choiceContentMap = choiceInfos.stream()
				.collect(Collectors.toMap(SurveyDetailDto.ChoiceInfo::choiceId, SurveyDetailDto.ChoiceInfo::content));

			return choices.stream()
				.map(choice -> {
					String content = choiceContentMap.get(choice.choiceId());
					return SelectChoiceStat.of(choice, content);
				})
				.toList();
		}

		private static SelectChoiceStat of(StatisticReport.ChoiceStatsResult choice, String content) {
			SelectChoiceStat stat = new	SelectChoiceStat();
			stat.choiceId = choice.choiceId();
			stat.choiceContent = content;
			stat.choiceCount = choice.count();
			stat.choiceRatio = String.format("%.1f%%", choice.ratio() * 100);
			return stat;
		}
	}

	@Getter
	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class TextStat implements ChoiceStat {
		private String text;

		public static List<TextStat> toStats(List<String> texts) {
			return texts.stream()
				.map(TextStat::from)
				.toList();
		}

		public static TextStat from(String text){
			TextStat stat = new TextStat();
			stat.text = text;
			return stat;
		}
	}

	@Getter
	@AllArgsConstructor
	public enum ChoiceType {
		SINGLE_CHOICE("선택형", AnswerType.SINGLE_CHOICE.getKey()),
		MULTIPLE_CHOICE("다중 선택형", AnswerType.MULTIPLE_CHOICE.getKey()),
		TEXT_ANSWER("텍스트", AnswerType.TEXT_ANSWER.getKey()),
		;

		private final String description;
		private final String key;

		public static ChoiceType findByKey(String key) {
			return Arrays.stream(values())
				.filter(type -> type.key.equals(key))
				.findFirst()
				.orElseThrow(() -> new CustomException(CustomErrorCode.ANSWER_TYPE_NOT_FOUND));
		}
	}
}
