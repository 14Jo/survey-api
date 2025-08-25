package com.example.surveyapi.share.application.notification.dto;

import java.time.LocalDateTime;

import com.example.surveyapi.share.domain.notification.entity.Notification;
import com.example.surveyapi.share.domain.notification.vo.Status;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NotificationResponse {
	private Long id;
	private Long recipientId;
	private Status status;
	private LocalDateTime sentAt;
	private String failedReason;

	public NotificationResponse(Notification notification) {
		this.id = notification.getId();
		this.recipientId = notification.getRecipientId();
		this.status = notification.getStatus();
		this.sentAt = notification.getSentAt();
		this.failedReason = notification.getFailedReason();
	}

	public static NotificationResponse from(Notification notification) {
		return new NotificationResponse(notification);
	}
}
