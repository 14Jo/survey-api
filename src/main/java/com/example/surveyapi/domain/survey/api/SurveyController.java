package com.example.surveyapi.domain.survey.api;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.surveyapi.domain.survey.application.SurveyQueryService;
import com.example.surveyapi.domain.survey.application.SurveyService;
import com.example.surveyapi.domain.survey.application.request.CreateSurveyRequest;
import com.example.surveyapi.domain.survey.application.response.SearchSurveyDtailResponse;
import com.example.surveyapi.domain.survey.application.response.SearchSurveyTitleResponse;
import com.example.surveyapi.global.util.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/survey")
@RequiredArgsConstructor
public class SurveyController {

	private final SurveyService surveyService;
	private final SurveyQueryService surveyQueryService;

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

	//TODO 수정자 ID 구현 필요
	@PatchMapping("/{surveyId}/open")
	public ResponseEntity<ApiResponse<String>> open(
		@PathVariable Long surveyId
	) {
		Long userId = 1L;
		String result = surveyService.open(surveyId, userId);

		return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("설문 시작 성공", result));
	}

	@PatchMapping("/{surveyId}/close")
	public ResponseEntity<ApiResponse<String>> close(
		@PathVariable Long surveyId
	) {
		Long userId = 1L;
		String result = surveyService.close(surveyId, userId);

		return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("설문 종료 성공", result));
	}

	@DeleteMapping("/{surveyId}/delete")
	public ResponseEntity<ApiResponse<String>> delete(
		@PathVariable Long surveyId
	) {
		Long userId = 1L;
		String result = surveyService.delete(surveyId, userId);

		return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("설문 삭제 성공", result));
	}

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
