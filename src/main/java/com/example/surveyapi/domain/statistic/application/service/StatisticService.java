package com.example.surveyapi.domain.statistic.application.service;

import org.springframework.stereotype.Service;

import com.example.surveyapi.domain.statistic.domain.model.aggregate.Statistics;
import com.example.surveyapi.domain.statistic.domain.repository.StatisticRepository;
import com.example.surveyapi.global.enums.CustomErrorCode;
import com.example.surveyapi.global.exception.CustomException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StatisticService {

	private final StatisticRepository statisticRepository;

	public void create(Long surveyId) {
		//TODO : survey 유효성 검사
		if (statisticRepository.existsById(surveyId)) {
			throw new CustomException(CustomErrorCode.STATISTICS_ALREADY_EXISTS);
		}
		Statistics statistic = Statistics.create(surveyId);
		statisticRepository.save(statistic);
	}

	public void calculateLiveStatistics() {

	}
}
