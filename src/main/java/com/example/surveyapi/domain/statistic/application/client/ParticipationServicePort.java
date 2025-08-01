package com.example.surveyapi.domain.statistic.application.client;

import java.util.List;

public interface ParticipationServicePort {

	List<ParticipationInfoDto> getParticipationInfos(String authHeader, List<Long> surveyIds);
}
