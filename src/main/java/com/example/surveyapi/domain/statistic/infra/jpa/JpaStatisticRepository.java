package com.example.surveyapi.domain.statistic.infra.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.surveyapi.domain.statistic.domain.model.aggregate.Statistics;

public interface JpaStatisticRepository extends JpaRepository<Statistics, Long> {
}
