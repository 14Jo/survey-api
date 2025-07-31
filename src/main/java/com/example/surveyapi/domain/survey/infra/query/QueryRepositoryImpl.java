package com.example.surveyapi.domain.survey.infra.query;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.example.surveyapi.domain.survey.domain.query.QueryRepository;
import com.example.surveyapi.domain.survey.domain.query.dto.SurveyDetail;
import com.example.surveyapi.domain.survey.domain.query.dto.SurveyTitle;
import com.example.surveyapi.domain.survey.infra.query.dsl.QueryDslRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class QueryRepositoryImpl implements QueryRepository {

	private final QueryDslRepository dslRepository;

	@Override
	public Optional<SurveyDetail> getSurveyDetail(Long surveyId) {
		return dslRepository.findSurveyDetailBySurveyId(surveyId);
	}

	@Override
	public List<SurveyTitle> getSurveyTitles(Long projectId, Long lastSurveyId) {
		return dslRepository.findSurveyTitlesInCursor(projectId, lastSurveyId);
	}

	@Override
	public List<SurveyTitle> getSurveys(List<Long> surveyIds) {
		return dslRepository.findSurveys(surveyIds);
	}
}
