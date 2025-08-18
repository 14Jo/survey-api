package com.example.surveyapi.domain.statistic.infra.elastic;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class StatisticElasticQueryRepository {

	private final ElasticsearchClient client;

	public List<Map<String, Object>> findBySurveyId(Long surveyId) {
		try {
			SearchRequest request = SearchRequest.of(s -> s
				.index("statistics")
				.query(q -> q
					.term(t -> t
						.field("surveyId")
						.value(surveyId)
					)
				)
				.size(1000)
			);

			// 🔑 여기서 제네릭 타입 명확히 지정
			SearchResponse<Map<String, Object>> response =
				client.search(request, (Class<Map<String, Object>>) (Class<?>) Map.class);

			return response.hits().hits().stream()
				.map(Hit::source)
				.collect(Collectors.toList());

		} catch (IOException e) {
			throw new RuntimeException("Elasticsearch 조회 중 오류 발생", e);
		}
	}
}
