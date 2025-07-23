package com.example.surveyapi.domain.survey.api;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.surveyapi.domain.survey.application.SurveyQueryService;
import com.example.surveyapi.domain.survey.application.response.SearchSurveyDtailResponse;
import com.example.surveyapi.domain.survey.application.response.SearchSurveyTitleResponse;
import com.example.surveyapi.global.util.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/survey")
@RequiredArgsConstructor
public class SurveyQueryController {

	private final SurveyQueryService surveyQueryService;

	@GetMapping("/{surveyId}/detail")
	public ResponseEntity<ApiResponse<SearchSurveyDtailResponse>> getSurveyDetail(
		@PathVariable Long surveyId
	) {
		SearchSurveyDtailResponse surveyDetailById = surveyQueryService.findSurveyDetailById(surveyId);

		return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("조회 성공", surveyDetailById));
	}

	@GetMapping("/{projectId}/survey-list")
	public ResponseEntity<ApiResponse<List<SearchSurveyTitleResponse>>> getSurveyList(
		@PathVariable Long projectId,
		@RequestParam(required = false) Long lastSurveyId
	) {
		List<SearchSurveyTitleResponse> surveyByProjectId = surveyQueryService.findSurveyByProjectId(projectId, lastSurveyId);

		return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("조회 성공", surveyByProjectId));
	}
}
