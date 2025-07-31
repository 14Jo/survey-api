package com.example.surveyapi.domain.user.application.client;

import java.util.List;

public interface ParticipationPort {
    List<UserSurveyStatusResponse> getParticipationSurveyStatus(
        String authHeader, Long userId, int page, int size);
}
