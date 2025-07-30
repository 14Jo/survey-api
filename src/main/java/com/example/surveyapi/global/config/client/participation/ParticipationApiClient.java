package com.example.surveyapi.global.config.client.participation;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

import com.example.surveyapi.domain.statistic.application.client.ParticipationRequestDto;
import com.example.surveyapi.global.config.client.ExternalApiResponse;

@HttpExchange
public interface ParticipationApiClient {

    @PostExchange("/api/v1/surveys/participations")
    ExternalApiResponse getParticipationInfos(
        @RequestHeader("Authorization") String authHeader,
        @RequestBody ParticipationRequestDto dto
    );

    @GetExchange("/api/v1/members/me/participations")
    ExternalApiResponse getSurveyStatus(
        @RequestHeader("Authorization") String authHeader,
        @RequestParam Long userId,
        @RequestParam("page") int page,
        @RequestParam("size") int size);
}
