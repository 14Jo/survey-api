package com.example.surveyapi.domain.participation.infra.adapter;

import org.springframework.stereotype.Component;

import com.example.surveyapi.domain.participation.application.client.UserServicePort;
import com.example.surveyapi.domain.participation.application.client.UserSnapshotDto;
import com.example.surveyapi.global.config.client.ExternalApiResponse;
import com.example.surveyapi.global.config.client.user.UserApiClient;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserServiceAdapter implements UserServicePort {

	private final UserApiClient userApiClient;
	private final ObjectMapper objectMapper;

	@Override
	public UserSnapshotDto getParticipantInfo(String authHeader, Long userId) {
		ExternalApiResponse userSnapshot = userApiClient.getParticipantInfo(authHeader, userId);
		Object rawData = userSnapshot.getOrThrow();

		return objectMapper.convertValue(rawData, new TypeReference<UserSnapshotDto>() {
		});
	}
}
