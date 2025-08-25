package com.example.surveyapi.domain.participation.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.surveyapi.domain.participation.application.ParticipationService;
import com.example.surveyapi.domain.participation.application.dto.request.CreateParticipationRequest;
import com.example.surveyapi.global.dto.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class ParticipationController {

	private final ParticipationService participationService;

	@PostMapping("/surveys/{surveyId}/participations")
	public ResponseEntity<ApiResponse<Long>> create(
		@RequestHeader("Authorization") String authHeader,
		@PathVariable Long surveyId,
		@Valid @RequestBody CreateParticipationRequest request,
		@AuthenticationPrincipal Long userId
	) {
		Long participationId = participationService.create(authHeader, surveyId, userId, request);

		return ResponseEntity.status(HttpStatus.CREATED)
			.body(ApiResponse.success("설문 응답 제출이 완료되었습니다.", participationId));
	}

	@PutMapping("/participations/{participationId}")
	public ResponseEntity<ApiResponse<Void>> update(
		@RequestHeader("Authorization") String authHeader,
		@PathVariable Long participationId,
		@Valid @RequestBody CreateParticipationRequest request,
		@AuthenticationPrincipal Long userId
	) {
		participationService.update(authHeader, userId, participationId, request);

		return ResponseEntity.status(HttpStatus.OK)
			.body(ApiResponse.success("참여 응답 수정이 완료되었습니다.", null));
	}
}
