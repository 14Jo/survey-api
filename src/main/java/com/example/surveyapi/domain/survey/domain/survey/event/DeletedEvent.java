package com.example.surveyapi.domain.survey.domain.survey.event;

import com.example.surveyapi.domain.survey.domain.survey.Survey;

/**
 * 설문 삭제 이벤트
 */
public class DeletedEvent {
    private final Survey survey;

    public DeletedEvent(Survey survey) {
        this.survey = survey;
    }

    public Long getSurveyId() {
        return survey.getSurveyId();
    }

    public Long getCreatorId() {
        return survey.getCreatorId();
    }

    public Survey getSurvey() {
        return survey;
    }
}