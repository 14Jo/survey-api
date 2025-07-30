package com.example.surveyapi.domain.user.application.client;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class UserSurveyStatusResponse {
    private Long surveyId;
    private String surveyStatus;
}
