package com.example.surveyapi.domain.statistic.api.controller;

import org.springframework.web.bind.annotation.RestController;

import com.example.surveyapi.domain.statistic.application.service.StatisticService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class StatisticController {

	private final StatisticService statisticService;

}
