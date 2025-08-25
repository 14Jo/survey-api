package com.example.surveyapi.share.application.notification.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.example.surveyapi.share.domain.notification.vo.ShareMethod;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class NotificationEmailCreateRequest {
	private ShareMethod shareMethod;
	private List<@Email String> emails;
	private LocalDateTime notifyAt;
}
