package com.example.surveyapi.domain.survey.application.qeury;

import java.util.List;

import com.example.surveyapi.domain.survey.application.qeury.dto.QuestionSyncDto;
import com.example.surveyapi.domain.survey.application.qeury.dto.SurveySyncDto;
import com.example.surveyapi.domain.survey.domain.survey.enums.SurveyStatus;

public interface SurveyReadSyncPort {

	void surveyReadSync(SurveySyncDto dto, List<QuestionSyncDto> questions);

	void updateSurveyRead(SurveySyncDto dto);

	void questionReadSync(Long surveyId, List<QuestionSyncDto> dtos);

	void deleteSurveyRead(Long surveyId);

	void updateSurveyStatus(Long surveyId, SurveyStatus status);
}
