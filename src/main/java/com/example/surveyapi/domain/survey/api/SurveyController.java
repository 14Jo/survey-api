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

	//TODO 생성자 ID 구현 필요
	@PostMapping("/{projectId}/create")
	public ResponseEntity<ApiResponse<Long>> create(
		@PathVariable Long projectId,
		@Valid @RequestBody CreateSurveyRequest request,
		@AuthenticationPrincipal Long creatorId
	) {
		Long surveyId = surveyService.create(projectId, creatorId, request);

		return ResponseEntity.status(HttpStatus.CREATED)
			.body(ApiResponse.success("설문 생성 성공", surveyId));
	}

	//TODO 수정자 ID 구현 필요
	@PatchMapping("/{surveyId}/open")
	public ResponseEntity<ApiResponse<String>> open(
		@PathVariable Long surveyId,
		@AuthenticationPrincipal Long creatorId
	) {
		String result = surveyService.open(surveyId, creatorId);

		return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("설문 시작 성공", result));
	}

	@PatchMapping("/{surveyId}/close")
	public ResponseEntity<ApiResponse<String>> close(
		@PathVariable Long surveyId,
		@AuthenticationPrincipal Long creatorId
	) {
		String result = surveyService.close(surveyId, creatorId);

		return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("설문 종료 성공", result));
	}

	@PutMapping("/{surveyId}/update")
	public ResponseEntity<ApiResponse<String>> update(
		@PathVariable Long surveyId,
		@Valid @RequestBody UpdateSurveyRequest request,
		@AuthenticationPrincipal Long creatorId
	) {
		String result = surveyService.update(surveyId, creatorId, request);

		return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("설문 수정 성공", result));
	}

	@DeleteMapping("/{surveyId}/delete")
	public ResponseEntity<ApiResponse<String>> delete(
		@PathVariable Long surveyId,
		@AuthenticationPrincipal Long creatorId
	) {
		String result = surveyService.delete(surveyId, creatorId);

		return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("설문 삭제 성공", result));
	}
}
