package com.example.surveyapi.domain.statistic.infra.elastic;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.example.surveyapi.domain.statistic.domain.statisticdocument.StatisticDocument;

public interface StatisticEsJpaRepository extends ElasticsearchRepository<StatisticDocument, Long> {
}
