package com.example.surveyapi.domain.share.infra.notification.sender;

import org.springframework.stereotype.Service;

import com.example.surveyapi.domain.share.application.notification.NotificationSendService;
import com.example.surveyapi.domain.share.domain.notification.entity.Notification;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationSendServiceImpl implements NotificationSendService {
	private final NotificationFactory factory;

	@Override
	public void send(Notification notification) {
		NotificationSender sender = factory.getSender(notification.getShareMethod());
		sender.send(notification);
	}
}
