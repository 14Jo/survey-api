package com.example.surveyapi.statistic.domain.statisticdocument;

import java.util.List;

public interface StatisticDocumentRepository {
	void saveAll(List<StatisticDocument> statisticDocuments);
}
