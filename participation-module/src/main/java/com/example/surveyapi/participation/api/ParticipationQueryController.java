package com.example.surveyapi.participation.api;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.surveyapi.participation.application.ParticipationQueryService;
import com.example.surveyapi.participation.application.dto.response.ParticipationDetailResponse;
import com.example.surveyapi.participation.application.dto.response.ParticipationGroupResponse;
import com.example.surveyapi.participation.application.dto.response.ParticipationInfoResponse;
import com.example.surveyapi.global.dto.ApiResponse;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class ParticipationQueryController {

	private final ParticipationQueryService participationQueryService;

	@GetMapping("/surveys/participations")
	public ResponseEntity<ApiResponse<List<ParticipationGroupResponse>>> getAllBySurveyIds(
		@RequestParam(required = true) List<Long> surveyIds
	) {
		List<ParticipationGroupResponse> result = participationQueryService.getAllBySurveyIds(surveyIds);

		return ResponseEntity.status(HttpStatus.OK)
			.body(ApiResponse.success("여러 참여 기록 조회에 성공하였습니다.", result));
	}

	@GetMapping("/members/me/participations")
	public ResponseEntity<ApiResponse<Page<ParticipationInfoResponse>>> getAll(
		@RequestHeader("Authorization") String authHeader,
		@AuthenticationPrincipal Long userId,
		Pageable pageable
	) {
		Page<ParticipationInfoResponse> result = participationQueryService.gets(authHeader, userId, pageable);

		return ResponseEntity.status(HttpStatus.OK)
			.body(ApiResponse.success("나의 참여 목록 조회에 성공하였습니다.", result));
	}

	@GetMapping("/participations/{participationId}")
	public ResponseEntity<ApiResponse<ParticipationDetailResponse>> get(
		@PathVariable Long participationId,
		@AuthenticationPrincipal Long userId
	) {
		ParticipationDetailResponse result = participationQueryService.get(userId, participationId);

		return ResponseEntity.status(HttpStatus.OK)
			.body(ApiResponse.success("참여 응답 상세 조회에 성공하였습니다.", result));
	}

	@GetMapping("/surveys/participations/count")
	public ResponseEntity<ApiResponse<Map<Long, Long>>> getParticipationCounts(
		@RequestParam List<Long> surveyIds
	) {
		Map<Long, Long> counts = participationQueryService.getCountsBySurveyIds(surveyIds);

		return ResponseEntity.status(HttpStatus.OK)
			.body(ApiResponse.success("참여 count 성공", counts));
	}
}
