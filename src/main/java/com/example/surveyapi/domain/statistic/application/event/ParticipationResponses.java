package com.example.surveyapi.domain.statistic.application.event;

import java.time.Instant;
import java.util.List;

public record ParticipationResponses(
	Long participationId,
	Long surveyId,
	Long userId,
	String userGender,
	String userBirthDate,
	Integer userAge,
	String userAgeGroup,
	Instant completedAt,
	List<Answer> answers
) {
	public record Answer(
		Long questionId,
		List<Long> choiceIds,
		String responseText
	) {}
}
