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
import com.example.surveyapi.global.util.ApiResponse;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v2")
public class ParticipationInternalController {

	private final ParticipationService participationService;

	@GetMapping("/surveys/participations/count")
	public ResponseEntity<ApiResponse<Map<Long, Long>>> getParticipationCounts(
		@RequestParam List<Long> surveyIds
	) {
		Map<Long, Long> counts = participationService.getCountsBySurveyIds(surveyIds);

		return ResponseEntity.status(HttpStatus.OK)
			.body(ApiResponse.success("참여 count 성공", counts));
	}
}
