package com.example.surveyapi.domain.survey.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.surveyapi.domain.survey.application.SurveyService;
import com.example.surveyapi.domain.survey.application.request.CreateSurveyRequest;
import com.example.surveyapi.domain.survey.application.request.UpdateSurveyRequest;
import com.example.surveyapi.global.util.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/survey")
@RequiredArgsConstructor
public class SurveyController {

	private final SurveyService surveyService;

	@PostMapping("/{projectId}/create")
	public ResponseEntity<ApiResponse<Long>> create(
		@PathVariable Long projectId,
		@Valid @RequestBody CreateSurveyRequest request,
		@AuthenticationPrincipal Long creatorId,
		@RequestHeader("Authorization") String authHeader
	) {
		Long surveyId = surveyService.create(authHeader, projectId, creatorId, request);

		return ResponseEntity.status(HttpStatus.CREATED)
			.body(ApiResponse.success("설문 생성 성공", surveyId));
	}

	@PatchMapping("/{surveyId}/open")
	public ResponseEntity<ApiResponse<String>> open(
		@PathVariable Long surveyId,
		@AuthenticationPrincipal Long creatorId,
		@RequestHeader("Authorization") String authHeader
	) {
		surveyService.open(authHeader, surveyId, creatorId);

		return ResponseEntity.status(HttpStatus.OK)
		.body(ApiResponse.success("설문 시작 성공", "X"));
	}

	@PatchMapping("/{surveyId}/close")
	public ResponseEntity<ApiResponse<String>> close(
		@PathVariable Long surveyId,
		@AuthenticationPrincipal Long creatorId,
		@RequestHeader("Authorization") String authHeader
	) {
		surveyService.close(authHeader, surveyId, creatorId);

		return ResponseEntity.status(HttpStatus.OK)
		.body(ApiResponse.success("설문 종료 성공", "X"));
	}

	@PutMapping("/{surveyId}/update")
	public ResponseEntity<ApiResponse<Long>> update(
		@PathVariable Long surveyId,
		@Valid @RequestBody UpdateSurveyRequest request,
		@AuthenticationPrincipal Long creatorId,
		@RequestHeader("Authorization") String authHeader
	) {
		Long updatedSurveyId = surveyService.update(authHeader, surveyId, creatorId, request);

		return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("설문 수정 성공", updatedSurveyId));
	}

	@DeleteMapping("/{surveyId}/delete")
	public ResponseEntity<ApiResponse<Long>> delete(
		@PathVariable Long surveyId,
		@AuthenticationPrincipal Long creatorId,
		@RequestHeader("Authorization") String authHeader
	) {
		Long deletedSurveyId = surveyService.delete(authHeader, surveyId, creatorId);

		return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("설문 삭제 성공", deletedSurveyId));
	}
}
