package com.example.surveyapi.global.client;

import java.util.List;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import com.example.surveyapi.global.dto.ExternalApiResponse;

@HttpExchange
public interface SurveyApiClient {

	@GetExchange("/api/v1/surveys/{surveyId}")
	ExternalApiResponse getSurveyDetail(
		@RequestHeader("Authorization") String authHeader,
		@PathVariable Long surveyId
	);

	@GetExchange("/api/v2/survey/find-surveys")
	ExternalApiResponse getSurveyInfoList(
		@RequestHeader("Authorization") String authHeader,
		@RequestParam List<Long> surveyIds
	);
}
