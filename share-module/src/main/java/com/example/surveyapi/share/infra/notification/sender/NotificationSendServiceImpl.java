package com.example.surveyapi.share.infra.notification.sender;

import org.springframework.stereotype.Service;

import com.example.surveyapi.share.application.notification.NotificationSendService;
import com.example.surveyapi.share.domain.notification.entity.Notification;

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
