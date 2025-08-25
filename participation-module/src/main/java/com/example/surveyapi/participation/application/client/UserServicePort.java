package com.example.surveyapi.participation.application.client;

public interface UserServicePort {
	UserSnapshotDto getParticipantInfo(String authHeader, Long userId);
}
