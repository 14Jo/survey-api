package com.example.surveyapi.domain.share.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.surveyapi.domain.share.application.ShareService;
import com.example.surveyapi.domain.share.application.dto.CreateShareRequest;
import com.example.surveyapi.domain.share.application.dto.ShareResponse;
import com.example.surveyapi.global.util.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/share-campaigns")
public class ShareController {
	private final ShareService shareService;

	@PostMapping
	public ResponseEntity<ApiResponse<ShareResponse>> createShare(@Valid @RequestBody CreateShareRequest request) {
		ShareResponse response = shareService.createShare(request.getSurveyId());
		ApiResponse<ShareResponse> body = ApiResponse.success("공유 캠페인 생성 완료", response);
		return ResponseEntity
			.status(HttpStatus.CREATED)
			.body(body);
	}
}
