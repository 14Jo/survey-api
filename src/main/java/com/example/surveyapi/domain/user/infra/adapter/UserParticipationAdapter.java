package com.example.surveyapi.domain.user.infra.adapter;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.example.surveyapi.domain.user.application.client.response.UserSurveyStatusResponse;
import com.example.surveyapi.domain.user.application.client.port.ParticipationPort;
import com.example.surveyapi.global.config.client.ExternalApiResponse;
import com.example.surveyapi.global.config.client.participation.ParticipationApiClient;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserParticipationAdapter implements ParticipationPort {

    private final ParticipationApiClient participationApiClient;
    private final ObjectMapper objectMapper;

    @Override
    public List<UserSurveyStatusResponse> getParticipationSurveyStatus(
        String authHeader, Long userId, int page, int size
    ) {
        ExternalApiResponse response = participationApiClient.getSurveyStatus(authHeader, userId, page, size);
        Object rawData = response.getOrThrow();

        Map<String, Object> mapData = objectMapper.convertValue(rawData, new TypeReference<Map<String, Object>>() {
        });

        List<UserSurveyStatusResponse> surveyStatusList =
            objectMapper.convertValue(
                mapData.get("content"),
                new TypeReference<List<UserSurveyStatusResponse>>() {
                }
            );
        return surveyStatusList;
    }


}
