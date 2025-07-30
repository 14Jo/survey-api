package com.example.surveyapi.domain.statistic.infra;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.example.surveyapi.domain.statistic.domain.model.aggregate.Statistic;
import com.example.surveyapi.domain.statistic.domain.repository.StatisticRepository;
import com.example.surveyapi.domain.statistic.infra.jpa.JpaStatisticRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class StatisticRepositoryImpl implements StatisticRepository {

	private final JpaStatisticRepository jpaStatisticRepository;

	@Override
	public Statistic save(Statistic statistic) {
		return jpaStatisticRepository.save(statistic);
	}

	@Override
	public boolean existsById(Long id) {
		return jpaStatisticRepository.existsById(id);
	}

	@Override
	public Optional<Statistic> findById(Long id) {
		return  jpaStatisticRepository.findById(id);
	}
}
