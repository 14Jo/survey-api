package com.example.surveyapi.domain.statistic.domain.query;

public record ChoiceStatistics(
	long choiceId,
	String content,
	long count
) {}