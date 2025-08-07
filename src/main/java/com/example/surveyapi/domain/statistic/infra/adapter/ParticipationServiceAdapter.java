package com.example.surveyapi.domain.statistic.infra.adapter;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.example.surveyapi.domain.statistic.application.client.ParticipationInfoDto;
import com.example.surveyapi.domain.statistic.application.client.ParticipationServicePort;
import com.example.surveyapi.domain.statistic.application.client.QuestionAnswers;
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
	public List<ParticipationInfoDto> getParticipationInfos(String authHeader, List<Long> surveyIds) {
		ExternalApiResponse response = participationApiClient.getParticipationInfos(authHeader, surveyIds);
		Object rawData = response.getOrThrow();

		List<ParticipationInfoDto> responses = objectMapper.convertValue(
			rawData,
			new TypeReference<List<ParticipationInfoDto>>() {
			}
		);

		return responses;
	}

	@Override
	public Map<Long, List<String>> getTextAnswersByQuestionIds(String authHeader, List<Long> questionIds) {
		ExternalApiResponse response = participationApiClient.getParticipationAnswers(authHeader, questionIds);
		Object rawData = response.getOrThrow();

		List<QuestionAnswers> responses = objectMapper.convertValue(
			rawData,
			new TypeReference<List<QuestionAnswers>>() {
			}
		);

		return responses.stream()
			.collect(Collectors.toMap(
				QuestionAnswers::questionId,
				qa -> qa.answers().stream()
					.map(QuestionAnswers.TextAnswer::textAnswer)
					.toList()
			));
	}
}
