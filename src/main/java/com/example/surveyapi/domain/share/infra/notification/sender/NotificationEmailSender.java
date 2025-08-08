package com.example.surveyapi.domain.share.infra.notification.sender;

import org.springframework.stereotype.Component;

import com.example.surveyapi.domain.share.domain.notification.entity.Notification;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("EMAIL")
public class NotificationEmailSender implements NotificationSender {
	@Override
	public void send(Notification notification) {
		log.info("이메일 전송: {}", notification.getId());
		// TODO : 이메일 실제 전송
	}
}
