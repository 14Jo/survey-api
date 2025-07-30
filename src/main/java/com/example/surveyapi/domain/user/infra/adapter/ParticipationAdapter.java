package com.example.surveyapi.domain.user.infra.adapter;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import com.example.surveyapi.domain.user.application.client.UserSurveyStatusResponse;
import com.example.surveyapi.domain.user.application.client.ParticipationPort;
import com.example.surveyapi.global.config.client.participation.ParticipationApiClient;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ParticipationAdapter implements ParticipationPort {

    private final ParticipationApiClient participationApiClient;

    @Override
    public List<UserSurveyStatusResponse> getParticipationSurveyStatus(
        String authHeader, Long userId, int page, int size
    ) {
        Page<UserSurveyStatusResponse> surveyStatus =
            participationApiClient.getSurveyStatus(authHeader, userId, page, size);

        return surveyStatus.getContent();
    }
}
