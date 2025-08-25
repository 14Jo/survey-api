package com.example.surveyapi.participation.infra.adapter;

import org.springframework.stereotype.Component;

import com.example.surveyapi.participation.application.client.UserServicePort;
import com.example.surveyapi.participation.application.client.UserSnapshotDto;
import com.example.surveyapi.global.client.UserApiClient;
import com.example.surveyapi.global.dto.ExternalApiResponse;
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
