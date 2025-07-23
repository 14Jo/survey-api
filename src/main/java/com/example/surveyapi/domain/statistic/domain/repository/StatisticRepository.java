package com.example.surveyapi.domain.statistic.domain.repository;

import com.example.surveyapi.domain.statistic.domain.model.aggregate.Statistics;

public interface StatisticRepository {

	//CRUD
	Statistics save(Statistics statistics);

	//exist
	boolean existsById(Long id);
}
