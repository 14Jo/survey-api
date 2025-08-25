package com.example.surveyapi.statistic.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.surveyapi.statistic.domain.statistic.Statistic;
import com.example.surveyapi.statistic.domain.statistic.StatisticRepository;
import com.example.surveyapi.global.exception.CustomErrorCode;
import com.example.surveyapi.global.exception.CustomException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatisticService {

	private final StatisticRepository statisticRepository;

	@Transactional
	public void create(Long surveyId) {
		if (statisticRepository.existsById(surveyId)) {
			throw new CustomException(CustomErrorCode.STATISTICS_ALREADY_EXISTS);
		}
		Statistic statistic = Statistic.start(surveyId);
		statisticRepository.save(statistic);
	}

	public Statistic getStatistic(Long surveyId) {
		return statisticRepository.findById(surveyId)
			.orElseThrow(() -> new CustomException(CustomErrorCode.STATISTICS_NOT_FOUND));
	}
}
