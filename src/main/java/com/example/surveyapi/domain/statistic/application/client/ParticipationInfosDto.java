package com.example.surveyapi.domain.statistic.application.client;

import java.util.List;
import java.util.Map;

public record ParticipationInfosDto(
	boolean success,
	String message,
	List<ParticipationDetailDto> data,
	String timestamp
) {
	public record ParticipationDetailDto(
		Long surveyId,
		List<SurveyResponseDto> responses
	) {}

	public record SurveyResponseDto(
		Long questionId,
		Map<String, Object> answer
	) {}
}