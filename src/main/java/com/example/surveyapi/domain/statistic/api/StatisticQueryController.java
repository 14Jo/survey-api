package com.example.surveyapi.domain.statistic.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.example.surveyapi.domain.statistic.application.StatisticQueryService;
import com.example.surveyapi.domain.statistic.domain.query.SurveyStatistics;
import com.example.surveyapi.global.util.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class StatisticQueryController {

	private final StatisticQueryService statisticQueryService;

	@GetMapping("/api/v2/surveys/{surveyId}/statistics/live")
	public ResponseEntity<ApiResponse<SurveyStatistics>> getLiveStatistics(
		@PathVariable Long surveyId
	) {
		SurveyStatistics liveStatistics = statisticQueryService.getSurveyStatistics(surveyId);

		return ResponseEntity.status(HttpStatus.OK)
			.body(ApiResponse.success("통계 조회 성공.", liveStatistics));
	}
}
