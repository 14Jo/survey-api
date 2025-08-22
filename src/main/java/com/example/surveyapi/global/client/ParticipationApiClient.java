package com.example.surveyapi.global.client;

import java.util.List;

import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import com.example.surveyapi.global.dto.ExternalApiResponse;

@HttpExchange
public interface ParticipationApiClient {

	@GetExchange("/api/v1/surveys/participations")
	ExternalApiResponse getParticipationInfos(
		@RequestHeader("Authorization") String authHeader,
		@RequestParam List<Long> surveyIds
	);

	@GetExchange("/api/v2/surveys/participations/count")
	ExternalApiResponse getParticipationCounts(
		@RequestParam List<Long> surveyIds
	);

	@GetExchange("/api/v2/participations/answers")
	ExternalApiResponse getParticipationAnswers(
		@RequestHeader("Authorization") String authHeader,
		@RequestParam List<Long> questionIds
	);
}
