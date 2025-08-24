package com.example.surveyapi.domain.survey.application.event;

public enum OutboxEventStatus {
	PENDING,
	SENT,
	PUBLISHED,
	FAILED
}