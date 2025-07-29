package com.example.surveyapi.domain.statistic.infra.adapter;

import org.springframework.stereotype.Component;

import com.example.surveyapi.domain.statistic.application.client.ParticipationInfosDto;
import com.example.surveyapi.domain.statistic.application.client.ParticipationRequestDto;
import com.example.surveyapi.domain.statistic.application.client.ParticipationServicePort;
import com.example.surveyapi.global.config.client.participation.ParticipationApiClient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ParticipationServiceAdapter implements ParticipationServicePort {

		private final ParticipationApiClient participationApiClient;

		@Override
		public ParticipationInfosDto getParticipationInfos(String authHeader, ParticipationRequestDto dto) {
			return participationApiClient.getParticipationInfos(authHeader, dto);
		}
	}
