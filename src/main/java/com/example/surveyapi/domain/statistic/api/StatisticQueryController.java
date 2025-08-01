package com.example.surveyapi.domain.statistic.api;

import org.springframework.web.bind.annotation.RestController;

import com.example.surveyapi.domain.statistic.application.StatisticQueryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class StatisticQueryController {

	private final StatisticQueryService statisticQueryService;
}
