package com.example.surveyapi.statistic.application;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.surveyapi.statistic.application.dto.StatisticBasicResponse;
import com.example.surveyapi.statistic.domain.query.QuestionStatistics;
import com.example.surveyapi.statistic.domain.query.StatisticQueryRepository;
import com.example.surveyapi.statistic.domain.statistic.Statistic;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatisticQueryService {

	private final StatisticQueryRepository repo;
	private final StatisticService statisticService;

	public StatisticBasicResponse getSurveyStatistics(Long surveyId) throws IOException {
		Statistic statistic = statisticService.getStatistic(surveyId);

		List<Map<String, Object>> initDocs = repo.findAllInitBySurveyId(surveyId);
		Map<Long, Map<Integer, Long>> choiceCounts = repo.aggregateChoiceCounts(surveyId);
		Map<Long, List<String>> textResponses = repo.findTextResponses(surveyId);

		List<QuestionStatistics> questionStats = QuestionStatistics.buildFrom(
			initDocs,
			choiceCounts,
			textResponses
		);

		List<StatisticBasicResponse.QuestionStat> dtoQuestions = questionStats.stream()
			.map(StatisticBasicResponse.QuestionStat::from)
			.sorted(Comparator.comparing(StatisticBasicResponse.QuestionStat::getQuestionId))
			.toList();

		return new StatisticBasicResponse(surveyId, statistic.getFinalResponseCount(), dtoQuestions);
	}
}