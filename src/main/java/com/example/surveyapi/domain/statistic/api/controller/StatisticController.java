package com.example.surveyapi.domain.statistic.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.surveyapi.domain.statistic.application.service.StatisticService;
import com.example.surveyapi.global.util.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class StatisticController {

	private final StatisticService statisticService;

	//TODO : 설문 종료되면 자동 실행
	@PostMapping("/api/v1/surveys/{surveyId}/statistics")
	public ResponseEntity<ApiResponse<Void>> create(@PathVariable Long surveyId) {
		statisticService.create(surveyId);

		return ResponseEntity.status(HttpStatus.CREATED)
			.body(ApiResponse.success("통계가 생성되었습니다.", null));
	}
	// public ResponseEntity<ApiResponse<Void>> fetchLiveStatistics() {
	// 	//TODO : Survey 도메인으로 부터 진행중인 설문 Id List 받아오기
	// }
}
