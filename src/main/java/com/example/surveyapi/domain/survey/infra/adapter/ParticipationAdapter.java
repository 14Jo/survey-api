package com.example.surveyapi.domain.survey.infra.adapter;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.example.surveyapi.domain.survey.application.client.ParticipationCountDto;
import com.example.surveyapi.domain.survey.application.client.ParticipationPort;
import com.example.surveyapi.global.config.client.ExternalApiResponse;
import com.example.surveyapi.global.config.client.participation.ParticipationApiClient;
import com.example.surveyapi.global.enums.CustomErrorCode;
import com.example.surveyapi.global.exception.CustomException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ParticipationAdapter implements ParticipationPort {

	private final ParticipationApiClient participationApiClient;
	private final ObjectMapper objectMapper;

	@Override
	public ParticipationCountDto getParticipationCounts(String authHeader, List<Long> surveyIds) {
		ExternalApiResponse participationCounts = participationApiClient.getParticipationCounts(authHeader, surveyIds);

		Map<Long, Long> rawData = convertToMap(participationCounts.getData());

		return ParticipationCountDto.of(rawData);
	}

	@SuppressWarnings("unchecked")
	private Map<Long, Long> convertToMap(Object data) {
		if (data instanceof Map) {
			return (Map<Long, Long>)data;
		}

		try {
			return objectMapper.convertValue(data, new TypeReference<Map<Long, Long>>() {
			});
		} catch (Exception e) {
			log.error("Map<Long, Long>으로 변환 실패: {}", data, e);
			throw new CustomException(CustomErrorCode.SERVER_ERROR, "참여자 수 데이터 변환 실패");
		}
	}
}
