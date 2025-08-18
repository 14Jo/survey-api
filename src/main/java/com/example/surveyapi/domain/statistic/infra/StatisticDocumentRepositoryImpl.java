package com.example.surveyapi.domain.statistic.infra;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.example.surveyapi.domain.statistic.domain.statisticdocument.StatisticDocument;
import com.example.surveyapi.domain.statistic.domain.statisticdocument.StatisticDocumentRepository;
import com.example.surveyapi.domain.statistic.infra.elastic.StatisticElasticQueryRepository;
import com.example.surveyapi.domain.statistic.infra.elastic.StatisticElasticRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class StatisticDocumentRepositoryImpl implements StatisticDocumentRepository {

	private final StatisticElasticRepository statisticElasticRepository;
	private final StatisticElasticQueryRepository statisticElasticQueryRepository;

	@Override
	public void saveAll(List<StatisticDocument> statisticDocuments) {
		statisticElasticRepository.saveAll(statisticDocuments);
	}

	@Override
	public List<Map<String, Object>> findBySurveyId(Long surveyId) {
		return statisticElasticQueryRepository.findBySurveyId(surveyId);
	}

}
