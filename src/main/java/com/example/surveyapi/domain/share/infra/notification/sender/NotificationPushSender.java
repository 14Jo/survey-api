package com.example.surveyapi.domain.share.infra.notification.sender;

import org.springframework.stereotype.Component;

import com.example.surveyapi.domain.share.domain.notification.entity.Notification;

@Component("PUSH")
public class NotificationPushSender implements NotificationSender {
	@Override
	public void send(Notification notification) {
		// TODO : 실제 PUSH 전송
	}
}
