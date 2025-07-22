package com.example.surveyapi.domain.survey.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.surveyapi.domain.survey.application.SurveyService;
import com.example.surveyapi.domain.survey.application.request.CreateSurveyRequest;
import com.example.surveyapi.global.util.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/survey")
@RequiredArgsConstructor
public class SurveyController {

	private final SurveyService surveyService;

	//TODO 생성자 ID 구현 필요
	@PostMapping("/{projectId}/create")
	public ResponseEntity<ApiResponse<Long>> create(
		@PathVariable Long projectId,
		@RequestBody CreateSurveyRequest request
	) {
		Long creatorId = 1L;
		Long surveyId = surveyService.create(projectId, creatorId, request);

		return ResponseEntity.status(HttpStatus.CREATED)
			.body(ApiResponse.success("설문 생성 성공", surveyId));
	}
}
