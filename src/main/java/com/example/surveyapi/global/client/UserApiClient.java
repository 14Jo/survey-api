package com.example.surveyapi.global.client;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import com.example.surveyapi.global.dto.ExternalApiResponse;

@HttpExchange
public interface UserApiClient {

	@GetExchange("/users/{userId}/snapshot")
	ExternalApiResponse getParticipantInfo(
		@RequestHeader("Authorization") String authHeader,
		@PathVariable Long userId
	);

	@GetExchange("/users/by-email")
	ExternalApiResponse getUserByEmail(
		@RequestHeader("Authorization") String authHeader,
		@RequestParam("email") String email
	);
}
