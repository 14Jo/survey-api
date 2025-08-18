package com.example.surveyapi.domain.statistic.infra.rabbitmq;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

public record ParticipationCreatedEvent(
	Long participationId,
	Long surveyId,
	Long userId,
	Demographic demographic,
	@JsonFormat(shape = JsonFormat.Shape.ARRAY)
	LocalDateTime completedAt,
	List<Answer> answers
) {
	public record Demographic(
		@JsonFormat(shape = JsonFormat.Shape.ARRAY)
		List<Integer> birth,
		String gender,
		Region region
	) {}

	public record Region(
		String province,
		String district
	) {}

	public record Answer(
		Long questionId,
		List<Long> choiceIds,
		String responseText
	) {}
}
