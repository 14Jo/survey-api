package com.example.surveyapi.domain.share.infra.adapter;

import org.springframework.stereotype.Component;

import com.example.surveyapi.domain.share.application.client.UserEmailDto;
import com.example.surveyapi.domain.share.application.client.UserServicePort;
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
	public UserEmailDto getUserByEmail(String authHeader, String email) {
		ExternalApiResponse userResponse = userApiClient.getUserByEmail(authHeader, email);
		Object rawData = userResponse.getOrThrow();

		return objectMapper.convertValue(rawData, new TypeReference<UserEmailDto>() {});
	}
}
