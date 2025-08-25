package com.example.surveyapi.global.client;

import java.util.List;

import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import com.example.surveyapi.global.dto.ExternalApiResponse;

@HttpExchange
public interface ParticipationApiClient {

	@GetExchange("/api/surveys/participations")
	ExternalApiResponse getParticipationInfos(
		@RequestHeader("Authorization") String authHeader,
		@RequestParam List<Long> surveyIds
	);

	@GetExchange("/api/surveys/participations/count")
	ExternalApiResponse getParticipationCounts(
		@RequestParam List<Long> surveyIds
	);
}
