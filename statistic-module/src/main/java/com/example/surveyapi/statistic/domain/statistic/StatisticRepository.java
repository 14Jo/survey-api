package com.example.surveyapi.statistic.domain.statistic;

import java.util.Optional;

public interface StatisticRepository {

	//CRUD
	Statistic save(Statistic statistic);
	Optional<Statistic> findById(Long id);

	//exist
	boolean existsById(Long id);
}
