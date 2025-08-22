package com.example.surveyapi.domain.survey.infra.adapter;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.example.surveyapi.domain.survey.application.client.ParticipationCountDto;
import com.example.surveyapi.domain.survey.application.client.ParticipationPort;
import com.example.surveyapi.global.dto.ExternalApiResponse;
import com.example.surveyapi.global.client.ParticipationApiClient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("surveyParticipationAdapter")
@RequiredArgsConstructor
public class ParticipationAdapter implements ParticipationPort {

	private final ParticipationApiClient participationApiClient;

	@Override
	public ParticipationCountDto getParticipationCounts(List<Long> surveyIds) {
		ExternalApiResponse participationCounts = participationApiClient.getParticipationCounts(surveyIds);

		@SuppressWarnings("unchecked")
		Map<String, Integer> rawData = (Map<String, Integer>)participationCounts.getData();

		return ParticipationCountDto.of(rawData);
	}
}
