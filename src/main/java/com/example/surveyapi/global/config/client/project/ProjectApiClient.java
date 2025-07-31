package com.example.surveyapi.global.config.client.project;

import java.util.List;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import com.example.surveyapi.global.config.client.ExternalApiResponse;

@HttpExchange
public interface ProjectApiClient {

	@GetExchange("/api/v2/projects/{projectId}/members")
	ExternalApiResponse getProjectMembers(
		@PathVariable Long projectId
	);

	@GetExchange("/api/v2/projects/{projectId}/state")
	ExternalApiResponse getProjectState(
		@PathVariable Long projectId
	);
}
