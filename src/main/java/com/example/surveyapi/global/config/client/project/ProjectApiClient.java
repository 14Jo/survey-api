package com.example.surveyapi.global.config.client.project;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;

import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.HttpExchange;

import com.example.surveyapi.global.config.client.ExternalApiResponse;

@HttpExchange
public interface ProjectApiClient {

	@GetExchange("/api/v2/projects//me/managers")
	ExternalApiResponse getProjectMembers(
		@RequestHeader("Authorization") String authHeader
	);

	@GetExchange("/api/v2/projects/{projectId}")
	ExternalApiResponse getProjectState(
		@RequestHeader("Authorization") String authHeader,
		@PathVariable Long projectId
	);

	@GetExchange("/api/v2/projects/me/managers")
	ExternalApiResponse getProjectMyRole(
		@RequestHeader("Authorization") String authHeader,
		@RequestParam Long userId);
}
