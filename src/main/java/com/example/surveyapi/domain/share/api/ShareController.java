package com.example.surveyapi.domain.share.api;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.example.surveyapi.domain.share.application.notification.NotificationService;
import com.example.surveyapi.domain.share.application.notification.dto.NotificationEmailCreateRequest;
import com.example.surveyapi.domain.share.application.notification.dto.NotificationResponse;
import com.example.surveyapi.domain.share.application.share.ShareService;
import com.example.surveyapi.domain.share.application.share.dto.ShareResponse;
import com.example.surveyapi.global.dto.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class ShareController {
	private final ShareService shareService;
	private final NotificationService notificationService;

	@PostMapping("/share-tasks/{shareId}/notifications")
	public ResponseEntity<ApiResponse<Void>> createNotifications(
		@RequestHeader("Authorization") String authHeader,
		@PathVariable Long shareId,
		@Valid @RequestBody NotificationEmailCreateRequest request,
		@AuthenticationPrincipal Long creatorId
	) {
		shareService.createNotifications(
			authHeader, shareId,
			creatorId, request.getShareMethod(),
			request.getEmails(), request.getNotifyAt());

		return ResponseEntity
			.status(HttpStatus.CREATED)
			.body(ApiResponse.success("알림 생성 성공", null));
	}

	@GetMapping("/share-tasks/{shareId}")
	public ResponseEntity<ApiResponse<ShareResponse>> get(
		@PathVariable Long shareId,
		@AuthenticationPrincipal Long currentUserId
	) {
		ShareResponse response = shareService.getShare(shareId, currentUserId);

		return ResponseEntity
			.status(HttpStatus.OK)
			.body(ApiResponse.success("공유 작업 조회 성공", response));
	}

	@GetMapping("/share-tasks/{shareId}/notifications")
	public ResponseEntity<ApiResponse<Page<NotificationResponse>>> getAll(
		@PathVariable Long shareId,
		@AuthenticationPrincipal Long currentId,
		Pageable pageable
	) {
		Page<NotificationResponse> response = notificationService.gets(shareId, currentId, pageable);

		return ResponseEntity.ok(ApiResponse.success("알림 이력 조회 성공", response));
	}

	@GetMapping("/notifications")
	public ResponseEntity<ApiResponse<Page<NotificationResponse>>> getMyNotifications(
		@AuthenticationPrincipal Long currentId,
		Pageable pageable
	) {
		Page<NotificationResponse> response =  notificationService.getMyNotifications(currentId, pageable);

		return ResponseEntity.status(HttpStatus.OK)
			.body(ApiResponse.success("알림 조회 성공", response));
	}
}
