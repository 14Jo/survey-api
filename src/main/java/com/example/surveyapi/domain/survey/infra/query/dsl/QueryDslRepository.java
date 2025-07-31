package com.example.surveyapi.domain.survey.infra.query.dsl;

import java.util.List;
import java.util.Optional;

import com.example.surveyapi.domain.survey.domain.query.dto.SurveyDetail;
import com.example.surveyapi.domain.survey.domain.query.dto.SurveyTitle;

public interface QueryDslRepository {

	Optional<SurveyDetail> findSurveyDetailBySurveyId(Long surveyId);

	List<SurveyTitle> findSurveyTitlesInCursor(Long projectId, Long lastSurveyId);

	List<SurveyTitle> findSurveys(List<Long> surveyIds);
}
