package com.example.surveyapi.domain.statistic.application.service;

import org.springframework.stereotype.Service;

import com.example.surveyapi.domain.statistic.domain.repository.StatisticRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StatisticService {

	private final StatisticRepository statisticRepository;

}
