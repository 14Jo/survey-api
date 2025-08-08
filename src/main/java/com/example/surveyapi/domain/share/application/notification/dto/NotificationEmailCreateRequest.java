package com.example.surveyapi.domain.share.application.notification.dto;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class NotificationEmailCreateRequest {
	private List<@Email String> emails;
	private LocalDateTime notifyAt;
}
