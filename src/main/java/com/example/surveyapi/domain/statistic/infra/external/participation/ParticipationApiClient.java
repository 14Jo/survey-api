package com.example.surveyapi.domain.statistic.infra.external.participation;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

import com.example.surveyapi.domain.statistic.application.client.ParticipationInfosDto;
import com.example.surveyapi.domain.statistic.application.client.ParticipationRequestDto;

@HttpExchange
public interface ParticipationApiClient {

	@PostExchange("/api/v1/surveys/participations")
	ParticipationInfosDto getParticipationInfos (
		@RequestHeader("Authorization") String authHeader,
		@RequestBody ParticipationRequestDto dto
	);
}
