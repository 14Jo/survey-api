package com.example.surveyapi.domain.share.application.notification.dto;

import java.util.List;

import org.springframework.data.domain.Page;

import com.example.surveyapi.domain.share.domain.notification.entity.Notification;
import com.example.surveyapi.global.util.PageInfo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NotificationPageResponse {
	private final List<NotificationResponse> content;
	private final PageInfo pageInfo;

	public static NotificationPageResponse from(Page<Notification> notifications) {
		List<NotificationResponse> content = notifications
			.stream()
			.map(NotificationResponse::from)
			.toList();

		PageInfo pageInfo = new PageInfo(
			notifications.getSize(),
			notifications.getNumber(),
			notifications.getTotalElements(),
			notifications.getTotalPages()
		);

		return new NotificationPageResponse(content, pageInfo);
	}
}
