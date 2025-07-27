package com.example.surveyapi.domain.participation.api;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.surveyapi.domain.participation.application.ParticipationService;
import com.example.surveyapi.domain.participation.application.dto.request.CreateParticipationRequest;
import com.example.surveyapi.domain.participation.application.dto.request.ParticipationGroupRequest;
import com.example.surveyapi.domain.participation.application.dto.response.ParticipationDetailResponse;
import com.example.surveyapi.domain.participation.application.dto.response.ParticipationGroupResponse;
import com.example.surveyapi.domain.participation.application.dto.response.ParticipationInfoResponse;
import com.example.surveyapi.global.util.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class ParticipationController {

	private final ParticipationService participationService;

	@PostMapping("/surveys/{surveyId}/participations")
	public ResponseEntity<ApiResponse<Long>> create(
		@PathVariable Long surveyId,
		@Valid @RequestBody CreateParticipationRequest request,
		@AuthenticationPrincipal Long memberId
	) {
		Long participationId = participationService.create(surveyId, memberId, request);

		return ResponseEntity.status(HttpStatus.CREATED)
			.body(ApiResponse.success("설문 응답 제출이 완료되었습니다.", participationId));
	}

	@GetMapping("/members/me/participations")
	public ResponseEntity<ApiResponse<Page<ParticipationInfoResponse>>> getAll(
		@AuthenticationPrincipal Long memberId,
		Pageable pageable
	) {
		Page<ParticipationInfoResponse> result = participationService.gets(memberId, pageable);

		return ResponseEntity.status(HttpStatus.OK)
			.body(ApiResponse.success("나의 전체 참여 응답 목록 조회에 성공하였습니다.", result));
	}

	@PostMapping("/surveys/participations/search")
	public ResponseEntity<ApiResponse<List<ParticipationGroupResponse>>> getAllBySurveyIds(
		@Valid @RequestBody ParticipationGroupRequest request
	) {
		List<ParticipationGroupResponse> result = participationService.getAllBySurveyIds(request.getSurveyIds());

		return ResponseEntity.status(HttpStatus.OK)
			.body(ApiResponse.success("여러 설문에 대한 모든 참여 응답 기록 조회에 성공하였습니다.", result));
	}

	@GetMapping("/participations/{participationId}")
	public ResponseEntity<ApiResponse<ParticipationDetailResponse>> get(
		@PathVariable Long participationId,
		@AuthenticationPrincipal Long memberId
	) {
		ParticipationDetailResponse result = participationService.get(memberId, participationId);

		return ResponseEntity.status(HttpStatus.OK)
			.body(ApiResponse.success("나의 참여 응답 상세 조회에 성공하였습니다.", result));
	}

	@PutMapping("/participations/{participationId}")
	public ResponseEntity<ApiResponse<Void>> update(
		@PathVariable Long participationId,
		@Valid @RequestBody CreateParticipationRequest request,
		@AuthenticationPrincipal Long memberId
	) {
		participationService.update(memberId, participationId, request);

		return ResponseEntity.status(HttpStatus.OK)
			.body(ApiResponse.success("참여 응답 수정이 완료되었습니다.", null));
	}
}
