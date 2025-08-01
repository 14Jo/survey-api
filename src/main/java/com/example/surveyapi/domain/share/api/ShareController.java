package com.example.surveyapi.domain.share.api;

import java.util.List;

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
		List<Long> recipientIds = List.of(2L, 3L, 4L);
		// TODO : 이벤트 처리 적용(위 리스트는 더미)
		ShareResponse response = shareService.createShare(
			request.getSurveyId(), creatorId,
			request.getShareMethod(), recipientIds);

		return ResponseEntity
			.status(HttpStatus.CREATED)
			.body(ApiResponse.success("공유 캠페인 생성 완료", response));
	}

	@GetMapping("/{shareId}")
	public ResponseEntity<ApiResponse<ShareResponse>> get(
		@PathVariable Long shareId,
		@AuthenticationPrincipal Long currentUserId
	) {
		ShareResponse response = shareService.getShare(shareId, currentUserId);

		return ResponseEntity
			.status(HttpStatus.OK)
			.body(ApiResponse.success("공유 작업 조회 성공", response));
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
