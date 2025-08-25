package com.example.surveyapi.statistic.infra.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.surveyapi.statistic.domain.statistic.Statistic;

public interface JpaStatisticRepository extends JpaRepository<Statistic, Long> {
}
