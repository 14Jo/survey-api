package com.example.surveyapi.domain.share.api.external;

import java.net.URI;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.surveyapi.domain.share.application.share.ShareService;
import com.example.surveyapi.domain.share.domain.share.entity.Share;
import com.example.surveyapi.domain.share.domain.share.vo.ShareSourceType;
import com.example.surveyapi.global.exception.CustomErrorCode;
import com.example.surveyapi.global.exception.CustomException;
import com.example.surveyapi.global.dto.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/share")
public class ShareExternalController {
	private final ShareService shareService;

	@GetMapping("{sourceType}/{sourceId}/link")
	public ResponseEntity<ApiResponse<String>> getLink(
		@PathVariable ShareSourceType sourceType,
		@PathVariable Long sourceId) {
		Share share = shareService.getShareBySource(sourceType, sourceId);

		return ResponseEntity.status(HttpStatus.OK)
			.body(ApiResponse.success("공유 링크 조회 성공", share.getLink()));
	}

	@GetMapping("/surveys/{token}")
	public ResponseEntity<Void> redirectToSurvey(@PathVariable String token) {
		String redirectUrl = shareService.getRedirectUrl(token, ShareSourceType.SURVEY);

		return ResponseEntity.status(HttpStatus.FOUND)
			.location(URI.create(redirectUrl)).build();
	}

	@GetMapping("/projects/members/{token}")
	public ResponseEntity<Void> redirectToProjectMember(@PathVariable String token) {
		String redirectUrl = shareService.getRedirectUrl(token, ShareSourceType.PROJECT_MEMBER);

		return ResponseEntity.status(HttpStatus.TEMPORARY_REDIRECT)
			.location(URI.create(redirectUrl)).build();
	}

	@GetMapping("/projects/managers/{token}")
	public ResponseEntity<Void> redirectToProjectManager(@PathVariable String token) {
		String redirectUrl = shareService.getRedirectUrl(token, ShareSourceType.PROJECT_MANAGER);

		return ResponseEntity.status(HttpStatus.TEMPORARY_REDIRECT)
			.location(URI.create(redirectUrl)).build();
	}
}
