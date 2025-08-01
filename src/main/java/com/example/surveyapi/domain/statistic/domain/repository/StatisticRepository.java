package com.example.surveyapi.domain.statistic.domain.repository;

import java.util.Optional;

import com.example.surveyapi.domain.statistic.domain.model.aggregate.Statistic;

public interface StatisticRepository {

	//CRUD
	Statistic save(Statistic statistic);
	Optional<Statistic> findById(Long id);

	//exist
	boolean existsById(Long id);
}
