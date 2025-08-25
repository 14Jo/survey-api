package com.example.surveyapi.domain.statistic.infra.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.surveyapi.domain.statistic.domain.statistic.Statistic;

public interface JpaStatisticRepository extends JpaRepository<Statistic, Long> {
}
