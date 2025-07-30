package com.example.surveyapi.domain.user.application.client;

import lombok.Getter;

@Getter
public class UserSurveyStatusResponse {
    private Long surveyId;
    private String surveyStatus;
}
