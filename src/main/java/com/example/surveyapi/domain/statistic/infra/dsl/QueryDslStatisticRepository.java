package com.example.surveyapi.domain.statistic.infra.dsl;

import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class QueryDslStatisticRepository {

	private final JPAQueryFactory queryFactory;

}
