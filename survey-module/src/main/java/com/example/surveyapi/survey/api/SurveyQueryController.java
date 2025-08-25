package com.example.surveyapi.survey.api;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.surveyapi.survey.application.dto.response.SearchSurveyDetailResponse;
import com.example.surveyapi.survey.application.dto.response.SearchSurveyStatusResponse;
import com.example.surveyapi.survey.application.dto.response.SearchSurveyTitleResponse;
import com.example.surveyapi.survey.application.qeury.SurveyReadService;
import com.example.surveyapi.global.dto.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SurveyQueryController {

	private final SurveyReadService surveyReadService;

	@GetMapping("/v1/surveys/{surveyId}")
	public ResponseEntity<ApiResponse<SearchSurveyDetailResponse>> getSurveyDetail(
		@PathVariable Long surveyId
	) {
		SearchSurveyDetailResponse surveyDetailById = surveyReadService.findSurveyDetailById(surveyId);

		return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("조회 성공", surveyDetailById));
	}

	@GetMapping("/v1/projects/{projectId}/surveys")
	public ResponseEntity<ApiResponse<List<SearchSurveyTitleResponse>>> getSurveyList(
		@PathVariable Long projectId,
		@RequestParam(required = false) Long lastSurveyId
	) {
		List<SearchSurveyTitleResponse> surveyByProjectId = surveyReadService.findSurveyByProjectId(projectId, lastSurveyId);

		return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("조회 성공", surveyByProjectId));
	}

	@GetMapping("/v2/survey/find-surveys")
	public ResponseEntity<ApiResponse<List<SearchSurveyTitleResponse>>> getSurveyList(
		@RequestParam List<Long> surveyIds
	) {
		List<SearchSurveyTitleResponse> surveys = surveyReadService.findSurveys(surveyIds);

		return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("조회 성공", surveys));
	}

	@GetMapping("/v2/survey/find-status")
	public ResponseEntity<ApiResponse<SearchSurveyStatusResponse>> getSurveyStatus(
		@RequestParam String surveyStatus
	) {
		SearchSurveyStatusResponse bySurveyStatus = surveyReadService.findBySurveyStatus(surveyStatus);

		return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("조회 성공", bySurveyStatus));
	}
}
