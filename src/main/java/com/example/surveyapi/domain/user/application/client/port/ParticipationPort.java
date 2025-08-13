package com.example.surveyapi.domain.user.application.client.port;

import java.util.List;

import com.example.surveyapi.domain.user.application.client.response.UserSurveyStatusResponse;

public interface ParticipationPort {
    List<UserSurveyStatusResponse> getParticipationSurveyStatus(
        String authHeader, Long userId, int page, int size);
}
