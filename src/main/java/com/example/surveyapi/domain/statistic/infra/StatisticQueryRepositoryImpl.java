package com.example.surveyapi.domain.statistic.infra;

import org.springframework.stereotype.Repository;

import com.example.surveyapi.domain.statistic.domain.repository.StatisticQueryRepository;
import com.example.surveyapi.domain.statistic.infra.dsl.QueryDslStatisticRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class StatisticQueryRepositoryImpl implements StatisticQueryRepository {

	private final QueryDslStatisticRepository QStatisticRepository;
}
