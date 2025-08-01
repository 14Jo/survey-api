package com.example.surveyapi.domain.participation.application.client;

public interface UserServicePort {
	UserSnapshotDto getParticipantInfo(String authHeader, Long userId);
}
