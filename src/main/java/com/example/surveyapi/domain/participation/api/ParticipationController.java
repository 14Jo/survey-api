package com.example.surveyapi.domain.participation.api;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.surveyapi.domain.participation.application.ParticipationService;
import com.example.surveyapi.domain.participation.application.dto.request.CreateParticipationRequest;
import com.example.surveyapi.domain.participation.application.dto.response.ReadParticipationPageResponse;
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
		@RequestBody @Valid CreateParticipationRequest request, @PathVariable Long surveyId) {
		Long memberId = 1L; // TODO: 시큐리티 적용후 사용자 인증정보에서 가져오도록 수정
		Long participationId = participationService.create(surveyId, memberId, request);

		return ResponseEntity.status(HttpStatus.CREATED)
			.body(ApiResponse.success("설문 응답 제출이 완료되었습니다.", participationId));
	}

	@GetMapping("/members/me/participations")
	public ResponseEntity<ApiResponse<Page<ReadParticipationPageResponse>>> getAll(
		@AuthenticationPrincipal Long memberId,
		@PageableDefault(size = 5, sort = "updatedAt", direction = Sort.Direction.DESC) Pageable pageable) {
		Long sampleMemberId = 1L;

		return ResponseEntity.status(HttpStatus.OK)
			.body(ApiResponse.success("나의 전체 설문 참여 기록 조회에 성공하였습니다.",
				participationService.gets(sampleMemberId, pageable)));
	}
}
