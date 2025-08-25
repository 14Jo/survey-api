package com.example.surveyapi.statistic.infra.elastic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.stereotype.Repository;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
import co.elastic.clients.elasticsearch._types.aggregations.LongTermsAggregate;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class StatisticEsClientRepository {

	private final ElasticsearchClient client;

	public List<Map<String, Object>> findAllInitBySurveyId(Long surveyId) throws IOException {
		SearchRequest request = SearchRequest.of(s -> s
			.index("statistics")
			.query(q -> q
				.bool(b -> b
					.must(t -> t.term(term -> term.field("surveyId").value(String.valueOf(surveyId))))
					.must(t -> t.wildcard(w -> w.field("responseId").value("*-init")))
				)
			)
			.size(1000)
		);

		SearchResponse<Object> response = client.search(request, Object.class);

		return response.hits().hits().stream()
			.map(Hit::source)
			.map(src -> (Map<String, Object>) src)
			.toList();
	}

	// ---------------- 선택형 집계 ----------------
	public Map<Long, Map<Integer, Long>> aggregateChoiceCounts(Long surveyId) throws IOException {
		Aggregation byQuestionAgg = Aggregation.of(a -> a
			.terms(t -> t.field("questionId"))
			.aggregations("by_choice", agg -> agg.terms(tt -> tt.field("choiceId")))
		);

		SearchRequest request = SearchRequest.of(s -> s
			.index("statistics")
			.query(q -> q
				.bool(b -> b
					.must(t -> t.term(term -> term.field("surveyId").value(surveyId)))
					.mustNot(n -> n.wildcard(w -> w.field("responseId.keyword").value("*-init")))
				)
			)
			.size(0)
			.aggregations("by_question", byQuestionAgg)
		);

		SearchResponse<Void> response = client.search(request, Void.class);

		Map<Long, Map<Integer, Long>> result = new HashMap<>();

		var byQuestionRaw = response.aggregations().get("by_question");
		if (byQuestionRaw != null && byQuestionRaw.isLterms()) {
			LongTermsAggregate byQuestion = byQuestionRaw.lterms();
			for (var qBucket : byQuestion.buckets().array()) {
				Long questionId = qBucket.key();
				Map<Integer, Long> choiceCounts = new HashMap<>();

				var byChoiceRaw = qBucket.aggregations().get("by_choice");
				if (byChoiceRaw != null && byChoiceRaw.isLterms()) {
					LongTermsAggregate byChoice = byChoiceRaw.lterms();
					for (var cBucket : byChoice.buckets().array()) {
						Integer choiceId = (int) cBucket.key();
						Long count = cBucket.docCount();
						choiceCounts.put(choiceId, count);
					}
				}

				result.put(questionId, choiceCounts);
			}
		}

		return result;
	}

	// ---------------- 텍스트형 응답 ----------------
	public Map<Long, List<String>> findTextResponses(Long surveyId) throws IOException {
		// 1. 먼저 surveyId 기준으로 서술형 질문 가져오기
		SearchRequest metaRequest = SearchRequest.of(s -> s
			.index("statistics")
			.query(q -> q
				.bool(b -> b
					.must(t -> t.term(term -> term.field("surveyId").value(surveyId)))
					.mustNot(n -> n.wildcard(w -> w.field("responseId.keyword").value("*-init")))
				)
			)
			.size(1000) // 임시로 충분히 큰 수, 질문 메타를 가져오기 위해
			.source(src -> src.filter(f -> f.includes("questionId", "questionType", "responseText")))
		);

		SearchResponse<Map<String, Object>> metaResponse =
			client.search(metaRequest, (Class<Map<String, Object>>) (Class<?>) Map.class);

		// 2. 서술형 질문만 필터링
		Map<Long, List<String>> result = new HashMap<>();
		for (var hit : metaResponse.hits().hits()) {
			Map<String, Object> source = hit.source();
			String type = Objects.toString(source.get("questionType"), "");
			if ("LONG_ANSWER".equals(type) || "SHORT_ANSWER".equals(type)) {
				Long qId = ((Number) source.get("questionId")).longValue();
				String text = Objects.toString(source.get("responseText"), "");
				if (!text.isBlank()) {
					result.computeIfAbsent(qId, k -> new ArrayList<>())
						.add(text);
				}
			}
		}

		// 3. 각 질문별로 최대 100개까지만
		result.replaceAll((k, v) -> v.size() > 100 ? v.subList(0, 100) : v);

		return result;
	}

}