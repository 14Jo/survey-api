package com.example.surveyapi.survey.application.client;

import java.util.List;

public interface ParticipationPort {

	ParticipationCountDto getParticipationCounts(List<Long> surveyIds);
}
