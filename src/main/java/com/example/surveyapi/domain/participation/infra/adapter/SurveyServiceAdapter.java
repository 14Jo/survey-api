package com.example.surveyapi.domain.participation.infra.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import com.example.surveyapi.domain.participation.application.client.SurveyDetailDto;
import com.example.surveyapi.domain.participation.application.client.SurveyInfoDto;
import com.example.surveyapi.domain.participation.application.client.SurveyServicePort;
import com.example.surveyapi.global.client.SurveyApiClient;
import com.example.surveyapi.global.dto.ExternalApiResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SurveyServiceAdapter implements SurveyServicePort {

	private final CacheManager cacheManager;
	private final SurveyApiClient surveyApiClient;
	private final ObjectMapper objectMapper;

	@Cacheable(value = "surveyDetails", key = "#surveyId", sync = true)
	@Override
	public SurveyDetailDto getSurveyDetail(String authHeader, Long surveyId) {
		ExternalApiResponse surveyDetail = surveyApiClient.getSurveyDetail(authHeader, surveyId);
		Object rawData = surveyDetail.getOrThrow();

		return objectMapper.convertValue(rawData, new TypeReference<SurveyDetailDto>() {
		});
	}

	@Override
	public List<SurveyInfoDto> getSurveyInfoList(String authHeader, List<Long> surveyIds) {
		Cache surveyInfoCache = Objects.requireNonNull(cacheManager.getCache("surveyInfo"));

		List<SurveyInfoDto> result = new ArrayList<>();
		List<Long> missedIds = new ArrayList<>();

		for (Long id : surveyIds) {
			SurveyInfoDto cachedInfo = surveyInfoCache.get(id, SurveyInfoDto.class);
			if (cachedInfo != null) {
				result.add(cachedInfo);
			} else {
				missedIds.add(id);
			}
		}

		if (!missedIds.isEmpty()) {
			ExternalApiResponse surveyInfoList = surveyApiClient.getSurveyInfoList(authHeader, missedIds);
			Object rawData = surveyInfoList.getOrThrow();

			List<SurveyInfoDto> requireInfoList = objectMapper.convertValue(rawData,
				new TypeReference<List<SurveyInfoDto>>() {
				});

			requireInfoList.forEach(surveyInfo -> {
				surveyInfoCache.put(surveyInfo.getSurveyId(), surveyInfo);
				result.add(surveyInfo);
			});
		}

		return result;
	}
}

