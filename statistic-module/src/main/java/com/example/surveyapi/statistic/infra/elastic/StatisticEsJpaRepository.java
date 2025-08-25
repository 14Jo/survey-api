package com.example.surveyapi.statistic.infra.elastic;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.example.surveyapi.statistic.domain.statisticdocument.StatisticDocument;

public interface StatisticEsJpaRepository extends ElasticsearchRepository<StatisticDocument, Long> {
}
