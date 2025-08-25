package com.example.surveyapi.statistic.domain.query;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface StatisticQueryRepository {

	List<Map<String, Object>> findAllInitBySurveyId(Long surveyId) throws IOException;
	Map<Long, Map<Integer, Long>> aggregateChoiceCounts(Long surveyId) throws IOException;
	Map<Long, List<String>> findTextResponses(Long surveyId) throws IOException;

}
