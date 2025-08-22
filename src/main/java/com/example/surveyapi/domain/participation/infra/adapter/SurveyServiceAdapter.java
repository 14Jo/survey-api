package com.example.surveyapi.domain.participation.infra.adapter;

import java.util.List;

import org.springframework.stereotype.Component;

import com.example.surveyapi.domain.participation.application.client.SurveyDetailDto;
import com.example.surveyapi.domain.participation.application.client.SurveyInfoDto;
import com.example.surveyapi.domain.participation.application.client.SurveyServicePort;
import com.example.surveyapi.global.dto.ExternalApiResponse;
import com.example.surveyapi.global.client.SurveyApiClient;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SurveyServiceAdapter implements SurveyServicePort {

	private final SurveyApiClient surveyApiClient;
	private final ObjectMapper objectMapper;

	@Override
	public SurveyDetailDto getSurveyDetail(String authHeader, Long surveyId) {
		ExternalApiResponse surveyDetail = surveyApiClient.getSurveyDetail(authHeader, surveyId);
		Object rawData = surveyDetail.getOrThrow();

		return objectMapper.convertValue(rawData, new TypeReference<SurveyDetailDto>() {
		});
	}

	@Override
	public List<SurveyInfoDto> getSurveyInfoList(String authHeader, List<Long> surveyIds) {
		ExternalApiResponse surveyInfoList = surveyApiClient.getSurveyInfoList(authHeader, surveyIds);
		Object rawData = surveyInfoList.getOrThrow();

		return objectMapper.convertValue(rawData, new TypeReference<List<SurveyInfoDto>>() {
		});
	}
}
