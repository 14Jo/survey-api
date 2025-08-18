package com.example.surveyapi.domain.statistic.domain.statisticdocument.dto;

import java.time.Instant;
import java.util.List;

public record DocumentCreateCommand (
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
