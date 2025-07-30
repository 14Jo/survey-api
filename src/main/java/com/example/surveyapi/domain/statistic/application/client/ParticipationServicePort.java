package com.example.surveyapi.domain.statistic.application.client;

public interface ParticipationServicePort {

	ParticipationInfosDto getParticipationInfos(String authHeader, ParticipationRequestDto dto);
}
