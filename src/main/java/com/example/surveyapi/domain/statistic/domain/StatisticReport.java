package com.example.surveyapi.domain.statistic.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.example.surveyapi.domain.statistic.domain.model.entity.StatisticsItem;
import com.example.surveyapi.domain.statistic.domain.model.enums.AnswerType;

import lombok.Getter;

@Getter
public class StatisticReport {

	public record QuestionStatsResult(Long questionId, String answerType, int totalCount, List<ChoiceStatsResult> choiceCounts) {}
   	public record ChoiceStatsResult(Long choiceId, int count, double ratio) {}

	private final List<StatisticsItem> items;
	private final LocalDateTime firstResponseAt;
	private final LocalDateTime lastResponseAt;

	private StatisticReport(List<StatisticsItem> items) {
		this.items = items;
		if (this.items.isEmpty()) {
			this.firstResponseAt = null;
			this.lastResponseAt = null;
		} else {
			this.items.sort(Comparator.comparing(StatisticsItem::getStatisticHour));
			this.firstResponseAt = items.get(0).getStatisticHour();
			this.lastResponseAt = items.get(items.size() - 1).getStatisticHour();
		}
	}

	public static StatisticReport from(List<StatisticsItem> items) {
		return new StatisticReport(items);
	}

	public List<Map<String, Object>> mappingTemporalStat() {
		if (items.isEmpty()) {
			return Collections.emptyList();
		}

		return items.stream()
			.collect(Collectors.groupingBy(
				StatisticsItem::getStatisticHour,
				Collectors.summingInt(StatisticsItem::getCount)))
			.entrySet().stream()
			.map(entry -> Map.<String, Object>of(
				"timestamp", entry.getKey(),
				"count", entry.getValue()
			))
			.sorted(Comparator.comparing(map ->
				(LocalDateTime)map.get("timestamp")))
			.toList();
	}

	public Map<Long, QuestionStatsResult> mappingQuestionStat() {
		if (items.isEmpty()) {
			return Collections.emptyMap();
		}

		Map<Long, List<StatisticsItem>> itemsByQuestion = items.stream()
			.collect(Collectors.groupingBy(StatisticsItem::getQuestionId));

		return itemsByQuestion.entrySet().stream()
			.map(entry -> createQuestionResult(
				entry.getKey(), entry.getValue()))
			.collect(Collectors.toMap(
				QuestionStatsResult::questionId,
				Function.identity(),
				(ov, nv) -> ov,
				HashMap::new
			));
	}

	private QuestionStatsResult createQuestionResult(Long questionId, List<StatisticsItem> items) {
		int totalCounts = items.stream().mapToInt(StatisticsItem::getCount).sum();
		AnswerType type = items.get(0).getAnswerType();
		List<ChoiceStatsResult> choiceCounts = createChoiceResult(items, type, totalCounts);

		return new QuestionStatsResult(questionId, type.getKey(), totalCounts, choiceCounts);
	}

	private List<ChoiceStatsResult> createChoiceResult(List<StatisticsItem> items, AnswerType type, int totalCount) {
		if (type.equals(AnswerType.TEXT_ANSWER)) {
			return new ArrayList<>();
		}

		return items.stream()
			.filter(item -> item.getChoiceId() != null)
			.collect(Collectors.groupingBy(
				StatisticsItem::getChoiceId,
				Collectors.summingInt(StatisticsItem::getCount)))
			.entrySet().stream()
			.map(entry -> {
				double ratio = (totalCount == 0) ? 0.0 : (double)entry.getValue() / totalCount;
				return new ChoiceStatsResult(
					entry.getKey(), entry.getValue(), ratio);
			})
			.sorted(Comparator.comparing(ChoiceStatsResult::choiceId))
			.toList();
	}
}
