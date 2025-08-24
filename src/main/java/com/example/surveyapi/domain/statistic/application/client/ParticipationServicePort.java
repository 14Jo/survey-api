package com.example.surveyapi.domain.statistic.application.client;

import java.util.List;
import java.util.Map;

import com.example.surveyapi.domain.statistic.application.client.dto.ParticipationInfoDto;

public interface ParticipationServicePort {

	List<ParticipationInfoDto> getParticipationInfos(String authHeader, List<Long> surveyIds);
	Map<Long, List<String>> getTextAnswersByQuestionIds(String authHeader, List<Long> questionIds);
}
