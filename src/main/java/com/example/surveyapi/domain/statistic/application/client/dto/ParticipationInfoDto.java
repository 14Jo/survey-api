package com.example.surveyapi.domain.statistic.application.client.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public record ParticipationInfoDto(
	Long surveyId,
	List<ParticipationDetailDto> participations
) {
	//public record ParticipationInfoDto()
	public record ParticipationDetailDto(
		Long participationId,
		LocalDateTime participatedAt,
		List<SurveyResponseDto> responses
	) {}

	public record SurveyResponseDto(
		Long questionId,
		Map<String, Object> answer
	) {}
}