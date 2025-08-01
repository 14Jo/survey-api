package com.example.surveyapi.global.config.client.participation;

import java.util.List;

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

	@GetExchange("/api/v1/surveys/participations")
	ExternalApiResponse getParticipationInfos(
		@RequestHeader("Authorization") String authHeader,
		@RequestParam List<Long> surveyIds
	);

	@GetExchange("/api/v2/surveys/participations/count")
	ExternalApiResponse getParticipationCounts(
		@RequestHeader("Authorization") String authHeader,
		@RequestParam List<Long> surveyIds
	);

    @GetExchange("/api/v1/members/me/participations")
    ExternalApiResponse getSurveyStatus(
        @RequestHeader("Authorization") String authHeader,
        @RequestParam Long userId,
        @RequestParam("page") int page,
        @RequestParam("size") int size);
}
