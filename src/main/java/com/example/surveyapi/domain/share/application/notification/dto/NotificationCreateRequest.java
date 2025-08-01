package com.example.surveyapi.domain.share.application.notification.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class NotificationCreateRequest {
	private Long shareId;
	private List<Long> recipientIds;
}
