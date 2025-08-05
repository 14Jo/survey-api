package com.example.surveyapi.domain.share.infra.notification.sender;

import org.springframework.stereotype.Component;

import com.example.surveyapi.domain.share.domain.notification.entity.Notification;

@Component("EMAIL")
public class NotificationEmailSender implements NotificationSender {
	@Override
	public void send(Notification notification) {
		// TODO : 이메일 실제 전송
	}
}
