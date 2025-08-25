package com.example.surveyapi.survey.infra.adapter;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.example.surveyapi.survey.application.client.ParticipationCountDto;
import com.example.surveyapi.survey.application.client.ParticipationPort;
import com.example.surveyapi.global.client.ParticipationApiClient;
import com.example.surveyapi.global.dto.ExternalApiResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("surveyParticipationAdapter")
@RequiredArgsConstructor
public class ParticipationAdapter implements ParticipationPort {

	private final ParticipationApiClient participationApiClient;
	
	@Value("${jwt.statistic.token}")
	private String serviceToken;

	@Override
	public ParticipationCountDto getParticipationCounts(List<Long> surveyIds) {
		ExternalApiResponse participationCounts = participationApiClient.getParticipationCounts(surveyIds);

		@SuppressWarnings("unchecked")
		Map<String, Integer> rawData = (Map<String, Integer>)participationCounts.getData();

		return ParticipationCountDto.of(rawData);
	}
}
