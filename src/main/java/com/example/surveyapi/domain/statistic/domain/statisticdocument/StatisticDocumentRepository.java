package com.example.surveyapi.domain.statistic.domain.statisticdocument;

import java.util.List;
import java.util.Map;

public interface StatisticDocumentRepository {
	void saveAll(List<StatisticDocument> statisticDocuments);
	List<Map<String, Object>> findBySurveyId(Long surveyId);
}
