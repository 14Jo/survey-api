package com.example.surveyapi.domain.survey.domain.query;

import java.util.Optional;

import com.example.surveyapi.domain.survey.domain.query.dto.SurveyDetail;

public interface QueryRepository {

	Optional<SurveyDetail> getSurveyDetail(Long surveyId);
}
