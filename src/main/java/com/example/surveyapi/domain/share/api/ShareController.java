package com.example.surveyapi.domain.share.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.example.surveyapi.domain.share.application.notification.NotificationService;
import com.example.surveyapi.domain.share.application.notification.dto.NotificationPageResponse;
import com.example.surveyapi.domain.share.application.share.ShareService;
import com.example.surveyapi.domain.share.application.share.dto.CreateShareRequest;
import com.example.surveyapi.domain.share.application.share.dto.ShareResponse;
import com.example.surveyapi.global.util.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/share-tasks")
public class ShareController {
	private final ShareService shareService;
	private final NotificationService notificationService;

	@PostMapping
	public ResponseEntity<ApiResponse<ShareResponse>> createShare(
		@Valid @RequestBody CreateShareRequest request,
		@AuthenticationPrincipal Long creatorId
	) {
		ShareResponse response = shareService.createShare(request.getSurveyId(), creatorId);
		ApiResponse<ShareResponse> body = ApiResponse.success("공유 캠페인 생성 완료", response);

		return ResponseEntity
			.status(HttpStatus.CREATED)
			.body(body);
	}

	@GetMapping("/{shareId}")
	public ResponseEntity<ApiResponse<ShareResponse>> get(
		@PathVariable Long shareId,
		@AuthenticationPrincipal Long currentUserId
	) {
		ShareResponse response = shareService.getShare(shareId, currentUserId);
		ApiResponse<ShareResponse> body = ApiResponse.success("공유 작업 조회 성공", response);

		return ResponseEntity
			.status(HttpStatus.OK)
			.body(body);
	}

	@GetMapping("/{shareId}/notifications")
	public ResponseEntity<ApiResponse<NotificationPageResponse>> getAll(
		@PathVariable Long shareId,
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size,
		@AuthenticationPrincipal Long currentId
	) {
		NotificationPageResponse response = notificationService.gets(shareId, currentId, page, size);

		return ResponseEntity.ok(ApiResponse.success("알림 이력 조회 성공", response));
	}
}
