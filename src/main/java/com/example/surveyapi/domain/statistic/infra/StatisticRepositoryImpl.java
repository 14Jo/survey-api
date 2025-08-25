package com.example.surveyapi.domain.statistic.infra;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.example.surveyapi.domain.statistic.domain.query.StatisticQueryRepository;
import com.example.surveyapi.domain.statistic.domain.statistic.Statistic;
import com.example.surveyapi.domain.statistic.domain.statistic.StatisticRepository;
import com.example.surveyapi.domain.statistic.domain.statisticdocument.StatisticDocument;
import com.example.surveyapi.domain.statistic.domain.statisticdocument.StatisticDocumentRepository;
import com.example.surveyapi.domain.statistic.infra.elastic.StatisticEsClientRepository;
import com.example.surveyapi.domain.statistic.infra.elastic.StatisticEsJpaRepository;
import com.example.surveyapi.domain.statistic.infra.jpa.JpaStatisticRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class StatisticRepositoryImpl implements StatisticRepository, StatisticQueryRepository,
	StatisticDocumentRepository {

	private final JpaStatisticRepository jpaStatisticRepository;
	private final StatisticEsClientRepository clientRepository;
	private final StatisticEsJpaRepository statisticElasticRepository;

	// StatisticRepository
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

	// StatisticDocumentRepository
	@Override
	public void saveAll(List<StatisticDocument> statisticDocuments) {
		statisticElasticRepository.saveAll(statisticDocuments);
	}

	// StatisticQueryRepository
	@Override
	public List<Map<String, Object>> findAllInitBySurveyId(Long surveyId) throws IOException {
		return clientRepository.findAllInitBySurveyId(surveyId);
	}

	@Override
	public Map<Long, Map<Integer, Long>> aggregateChoiceCounts(Long surveyId) throws IOException {
		return clientRepository.aggregateChoiceCounts(surveyId);
	}

	@Override
	public Map<Long, List<String>> findTextResponses(Long surveyId) throws IOException {
		return clientRepository.findTextResponses(surveyId);
	}
}

