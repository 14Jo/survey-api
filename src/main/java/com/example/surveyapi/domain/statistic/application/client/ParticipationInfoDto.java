package com.example.surveyapi.domain.statistic.application.client;

import java.util.List;
import java.util.Map;

public record ParticipationInfoDto(
	Long surveyId,
	List<ParticipationDetailDto> participations
) {
	//public record ParticipationInfoDto()
	public record ParticipationDetailDto(
		Long participationId,
		List<SurveyResponseDto> responses
	) {}

	public record SurveyResponseDto(
		Long questionId,
		Map<String, Object> answer
	) {}
}