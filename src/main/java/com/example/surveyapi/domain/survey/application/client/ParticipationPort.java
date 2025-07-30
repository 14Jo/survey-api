package com.example.surveyapi.domain.survey.application.client;

import java.util.List;

public interface ParticipationPort {

	ParticipationCountDto getParticipationCounts(String authHeader, List<Long> surveyIds);
}
