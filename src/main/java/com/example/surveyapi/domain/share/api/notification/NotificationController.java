package com.example.surveyapi.domain.share.api.notification;

import java.awt.print.Pageable;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.surveyapi.domain.share.application.notification.NotificationService;
import com.example.surveyapi.domain.share.application.notification.dto.NotificationPageResponse;
import com.example.surveyapi.global.util.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/share-tasks")
public class NotificationController {
	private final NotificationService notificationService;

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
