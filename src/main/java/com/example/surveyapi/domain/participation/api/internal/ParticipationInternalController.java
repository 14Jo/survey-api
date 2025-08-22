package com.example.surveyapi.domain.participation.api.internal;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.surveyapi.domain.participation.application.ParticipationService;
import com.example.surveyapi.domain.participation.application.dto.response.ParticipationGroupResponse;
import com.example.surveyapi.global.util.ApiResponse;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class ParticipationInternalController {

	private final ParticipationService participationService;

	@GetMapping("/v1/surveys/participations")
	public ResponseEntity<ApiResponse<List<ParticipationGroupResponse>>> getAllBySurveyIds(
		@RequestParam(required = true) List<Long> surveyIds
	) {
		List<ParticipationGroupResponse> result = participationService.getAllBySurveyIds(surveyIds);

		return ResponseEntity.status(HttpStatus.OK)
			.body(ApiResponse.success("여러 참여 기록 조회에 성공하였습니다.", result));
	}

	@GetMapping("/v2/surveys/participations/count")
	public ResponseEntity<ApiResponse<Map<Long, Long>>> getParticipationCounts(
		@RequestParam List<Long> surveyIds
	) {
		Map<Long, Long> counts = participationService.getCountsBySurveyIds(surveyIds);

		return ResponseEntity.status(HttpStatus.OK)
			.body(ApiResponse.success("참여 count 성공", counts));
	}
}
