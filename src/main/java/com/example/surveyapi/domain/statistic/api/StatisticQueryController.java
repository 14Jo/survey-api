package com.example.surveyapi.domain.statistic.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.example.surveyapi.domain.statistic.application.StatisticQueryService;
import com.example.surveyapi.domain.statistic.application.dto.StatisticBasicResponse;
import com.example.surveyapi.global.dto.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class StatisticQueryController {

	private final StatisticQueryService statisticQueryService;

	@GetMapping("/api/surveys/{surveyId}/statistics/basic")
	public ResponseEntity<ApiResponse<StatisticBasicResponse>> getLiveStatistics(
		@PathVariable Long surveyId
	) throws Exception {
		StatisticBasicResponse liveStatistics = statisticQueryService.getSurveyStatistics(surveyId);

		return ResponseEntity.status(HttpStatus.OK)
			.body(ApiResponse.success("통계 조회 성공.", liveStatistics));
	}
}
