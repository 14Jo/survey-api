package com.example.surveyapi.domain.share.infra.notification.sender;

import org.springframework.stereotype.Component;

import com.example.surveyapi.domain.share.domain.notification.entity.Notification;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("PUSH")
public class NotificationPushSender implements NotificationSender {
	@Override
	public void send(Notification notification) {
		log.info("PUSH 전송: {}", notification.getId());
		// TODO : 실제 PUSH 전송
	}
}
