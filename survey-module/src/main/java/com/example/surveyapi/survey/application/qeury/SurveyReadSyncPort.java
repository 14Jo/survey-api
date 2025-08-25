package com.example.surveyapi.survey.application.qeury;

import java.util.List;

import com.example.surveyapi.survey.application.qeury.dto.QuestionSyncDto;
import com.example.surveyapi.survey.application.qeury.dto.SurveySyncDto;
import com.example.surveyapi.survey.domain.survey.enums.ScheduleState;
import com.example.surveyapi.survey.domain.survey.enums.SurveyStatus;

public interface SurveyReadSyncPort {

	void surveyReadSync(SurveySyncDto dto, List<QuestionSyncDto> questions);

	void updateSurveyRead(SurveySyncDto dto);

	void questionReadSync(Long surveyId, List<QuestionSyncDto> dtos);

	void deleteSurveyRead(Long surveyId);

	void activateSurveyRead(Long surveyId, SurveyStatus status);

	void updateScheduleState(Long surveyId, ScheduleState scheduleState, SurveyStatus surveyStatus);
}
