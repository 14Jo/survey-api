package com.example.surveyapi.domain.statistic.application;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.surveyapi.domain.statistic.application.client.ParticipationInfosDto;
import com.example.surveyapi.domain.statistic.application.client.ParticipationRequestDto;
import com.example.surveyapi.domain.statistic.application.client.ParticipationServicePort;
import com.example.surveyapi.domain.statistic.domain.model.aggregate.Statistics;
import com.example.surveyapi.domain.statistic.domain.repository.StatisticRepository;
import com.example.surveyapi.global.enums.CustomErrorCode;
import com.example.surveyapi.global.exception.CustomException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatisticService {

	private final StatisticRepository statisticRepository;
	private final ParticipationServicePort participationServicePort;

	public void create(Long surveyId) {
		//TODO : survey 유효성 검사
		if (statisticRepository.existsById(surveyId)) {
			throw new CustomException(CustomErrorCode.STATISTICS_ALREADY_EXISTS);
		}
		Statistics statistic = Statistics.create(surveyId);
		statisticRepository.save(statistic);
	}

	public void calculateLiveStatistics(String authHeader) {
		//TODO : Survey 도메인으로 부터 진행중인 설문 Id List 받아오기
		List<Long> surveyIds = new ArrayList<>();
		surveyIds.add(1L);
		surveyIds.add(2L);
		surveyIds.add(3L);

		ParticipationRequestDto request = new ParticipationRequestDto(surveyIds);
		ParticipationInfosDto participationInfos = participationServicePort.getParticipationInfos(authHeader, request);

		log.info("ParticipationInfos: {}", participationInfos);
	}
}
