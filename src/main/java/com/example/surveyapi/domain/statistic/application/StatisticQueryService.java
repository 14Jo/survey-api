package com.example.surveyapi.domain.statistic.application;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.surveyapi.domain.statistic.domain.query.ChoiceStatistics;
import com.example.surveyapi.domain.statistic.domain.query.QuestionStatistics;
import com.example.surveyapi.domain.statistic.domain.query.SurveyStatistics;
import com.example.surveyapi.domain.statistic.domain.statisticdocument.StatisticDocumentRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatisticQueryService {

	private final StatisticDocumentRepository statisticDocumentRepository;

	public SurveyStatistics getSurveyStatistics(Long surveyId) {
		List<Map<String, Object>> docs = statisticDocumentRepository.findBySurveyId(surveyId);

		// 전체 응답 수
		int totalResponseCount = docs.stream()
			.map(d -> (Integer) d.get("userId"))
			.collect(Collectors.toSet())
			.size();

		// questionId 별로 그룹핑
		Map<Integer, List<Map<String, Object>>> grouped = docs.stream()
			.collect(Collectors.groupingBy(d -> (Integer) d.get("questionId")));

		List<QuestionStatistics> questionStats = new ArrayList<>();

		for (Map.Entry<Integer, List<Map<String, Object>>> entry : grouped.entrySet()) {
			Integer qId = entry.getKey();
			List<Map<String, Object>> responses = entry.getValue();

			String qType = (String) responses.get(0).get("questionType");
			String qText = (String) responses.get(0).get("questionText");

			if (qType.equals("SINGLE_CHOICE") || qType.equals("MULTIPLE_CHOICE")) {
				Map<Integer, Long> choiceCount = responses.stream()
					.collect(Collectors.groupingBy(r -> (Integer) r.get("choiceId"), Collectors.counting()));

				int total = responses.size();
				List<ChoiceStatistics> choices = choiceCount.entrySet().stream()
					.map(e -> {
						String content = (String) responses.stream()
							.filter(r -> Objects.equals(r.get("choiceId"), e.getKey()))
							.findFirst().get().get("choiceText");
						double ratio = (double) e.getValue() / total * 100;
						return ChoiceStatistics.of(Long.valueOf(e.getKey()), content,
							e.getValue().intValue(), String.format("%.1f%%", ratio));
					}).toList();

				questionStats.add(new QuestionStatistics(Long.valueOf(qId), qText,
					qType.equals("SINGLE_CHOICE") ? "선택형" : "다중 선택형",
					total, choices));
			} else {
				List<ChoiceStatistics> texts = responses.stream()
					.map(r -> ChoiceStatistics.text((String) r.get("responseText")))
					.toList();

				questionStats.add(new QuestionStatistics(Long.valueOf(qId), qText,
					"텍스트", responses.size(), texts));
			}
		}

		return new SurveyStatistics(
			surveyId,
			"고객 만족도 설문조사", // 실제로는 Survey 도메인에서 가져오거나 ES 필드에서
			totalResponseCount,
			questionStats,
			LocalDateTime.now()
		);
	}
}