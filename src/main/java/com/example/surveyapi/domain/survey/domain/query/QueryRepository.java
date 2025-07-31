package com.example.surveyapi.domain.survey.domain.query;

import java.util.List;
import java.util.Optional;

import com.example.surveyapi.domain.survey.domain.query.dto.SurveyDetail;
import com.example.surveyapi.domain.survey.domain.query.dto.SurveyTitle;

public interface QueryRepository {

	Optional<SurveyDetail> getSurveyDetail(Long surveyId);

	List<SurveyTitle> getSurveyTitles(Long projectId, Long lastSurveyId);

	List<SurveyTitle> getSurveys(List<Long> surveyIds);
}
