package com.example.surveyapi.domain.statistic.infra.adapter;

import java.util.List;

import org.springframework.stereotype.Component;

import com.example.surveyapi.domain.statistic.application.client.ParticipationInfoDto;
import com.example.surveyapi.domain.statistic.application.client.ParticipationRequestDto;
import com.example.surveyapi.domain.statistic.application.client.ParticipationServicePort;
import com.example.surveyapi.global.config.client.ExternalApiResponse;
import com.example.surveyapi.global.config.client.participation.ParticipationApiClient;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ParticipationServiceAdapter implements ParticipationServicePort {

		private final ParticipationApiClient participationApiClient;
		private final ObjectMapper objectMapper;

		@Override
		public List<ParticipationInfoDto> getParticipationInfos(String authHeader, ParticipationRequestDto dto) {
			ExternalApiResponse response = participationApiClient.getParticipationInfos(authHeader, dto);
			Object rawData = response.getOrThrow();

			List<ParticipationInfoDto> responses = objectMapper.convertValue(
				rawData,
				new TypeReference<List<ParticipationInfoDto>>() {}
			);

			return responses;
		}
	}
