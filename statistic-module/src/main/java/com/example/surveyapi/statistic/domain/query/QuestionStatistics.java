package com.example.surveyapi.statistic.domain.query;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public record QuestionStatistics(
	long questionId,
	String content,
	String type,
	long responseCount,
	List<ChoiceStatistics> choices,
	List<String> texts
) {
	public static List<QuestionStatistics> buildFrom(
		List<Map<String, Object>> initDocs,
		Map<Long, Map<Integer, Long>> choiceCounts,
		Map<Long, List<String>> textResponses
	) {
		Map<Long, QuestionMeta> metaMap = new LinkedHashMap<>();
		for (var doc : initDocs) {
			long qId = ((Number) doc.get("questionId")).longValue();
			String qText = Objects.toString(doc.get("questionText"), "");
			String qType = Objects.toString(doc.get("questionType"), "");
			QuestionMeta qm = metaMap.computeIfAbsent(qId, k -> new QuestionMeta(qText, qType));
			if (qm.isChoiceType() && doc.containsKey("choiceId") && doc.get("choiceId") != null) {
				qm.choices.put(
					((Number) doc.get("choiceId")).intValue(),
					Objects.toString(doc.get("choiceText"), "")
				);
			}
		}

		return metaMap.entrySet().stream()
			.map(entry -> {
				long qId = entry.getKey();
				QuestionMeta meta = entry.getValue();
				return meta.toStatistics(qId, choiceCounts, textResponses);
			})
			.collect(Collectors.toList());
	}

	private static class QuestionMeta {
		final String text;
		final String type;
		final Map<Integer, String> choices = new LinkedHashMap<>();

		QuestionMeta(String text, String type) {
			this.text = text;
			this.type = type;
		}

		boolean isChoiceType() {
			return "SINGLE_CHOICE".equals(type) || "MULTIPLE_CHOICE".equals(type);
		}

		QuestionStatistics toStatistics(
			long qId,
			Map<Long, Map<Integer, Long>> allChoiceCounts,
			Map<Long, List<String>> allTextResponses
		) {
			if (isChoiceType()) {
				Map<Integer, Long> questionCounts = allChoiceCounts.getOrDefault(qId, Collections.emptyMap());
				List<ChoiceStatistics> choiceStats = this.choices.entrySet().stream()
					.sorted(Map.Entry.comparingByKey())
					.map(entry -> {
						long actualCount = questionCounts.getOrDefault(entry.getKey(), 1L) - 1;
						return new ChoiceStatistics(entry.getKey(), entry.getValue(), actualCount);
					})
					.collect(Collectors.toList());

				long totalResponses = choiceStats.stream().mapToLong(ChoiceStatistics::count).sum();
				return new QuestionStatistics(qId, this.text, this.type, totalResponses, choiceStats, null);
			} else {
				List<String> texts = allTextResponses.getOrDefault(qId, Collections.emptyList());
				return new QuestionStatistics(qId, this.text, this.type, texts.size(), null, texts);
			}
		}
	}
}