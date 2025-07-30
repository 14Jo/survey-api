package com.example.surveyapi.domain.statistic.domain.repository;

import com.example.surveyapi.domain.statistic.domain.model.aggregate.Statistic;

public interface StatisticRepository {

	//CRUD
	Statistic save(Statistic statistic);

	//exist
	boolean existsById(Long id);
}
