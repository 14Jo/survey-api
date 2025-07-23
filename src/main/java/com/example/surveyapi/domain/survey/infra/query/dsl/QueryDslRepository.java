package com.example.surveyapi.domain.survey.infra.query.dsl;

import java.util.Optional;

import com.example.surveyapi.domain.survey.domain.query.dto.SurveyDetail;

public interface QueryDslRepository {

	Optional<SurveyDetail> findSurveyDetailBySurveyId(Long surveyId);
}
