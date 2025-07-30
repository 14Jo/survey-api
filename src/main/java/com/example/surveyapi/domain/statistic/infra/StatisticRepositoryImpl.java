package com.example.surveyapi.domain.statistic.infra;

import org.springframework.stereotype.Repository;

import com.example.surveyapi.domain.statistic.domain.model.aggregate.Statistics;
import com.example.surveyapi.domain.statistic.domain.repository.StatisticRepository;
import com.example.surveyapi.domain.statistic.infra.jpa.JpaStatisticRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class StatisticRepositoryImpl implements StatisticRepository {

	private final JpaStatisticRepository jpaStatisticRepository;

	@Override
	public Statistics save(Statistics statistics) {
		return jpaStatisticRepository.save(statistics);
	}

	@Override
	public boolean existsById(Long id) {
		return jpaStatisticRepository.existsById(id);
	}
}
