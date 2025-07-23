package com.example.surveyapi.domain.share.application.notification.dto;

import java.util.List;

import org.springframework.data.domain.Page;

import com.example.surveyapi.domain.share.domain.notification.entity.Notification;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NotificationPageResponse {
	private final List<NotificationResponse> content;
	private final int page;
	private final int size;
	private final long totalElements;
	private final int totalPages;

	public static NotificationPageResponse from(Page<Notification> notifications) {
		List<NotificationResponse> content = notifications
			.stream()
			.map(NotificationResponse::from)
			.toList();

		return new NotificationPageResponse(
			content,
			notifications.getNumber(),
			notifications.getSize(),
			notifications.getTotalElements(),
			notifications.getTotalPages()
		);
	}
}
