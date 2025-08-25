package com.example.surveyapi.domain.survey.application.event.enums;

public enum OutboxEventStatus {
	PENDING,
	SENT,
	PUBLISHED,
	FAILED
}