package com.example.surveyapi.share.infra.notification.sender;

import com.example.surveyapi.share.domain.notification.entity.Notification;

public interface NotificationSender {
	void send(Notification notification);
}
