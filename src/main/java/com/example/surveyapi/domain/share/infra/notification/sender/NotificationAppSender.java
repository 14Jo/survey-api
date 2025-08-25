package com.example.surveyapi.domain.share.infra.notification.sender;

import org.springframework.stereotype.Component;

import com.example.surveyapi.domain.share.domain.notification.entity.Notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("APP")
@RequiredArgsConstructor
public class NotificationAppSender implements NotificationSender {
	@Override
	public void send(Notification notification) {
		log.info("APP notification is created.");
	}
}
