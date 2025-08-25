package com.example.surveyapi.statistic.domain.query;

public record ChoiceStatistics(
	long choiceId,
	String content,
	long count
) {}