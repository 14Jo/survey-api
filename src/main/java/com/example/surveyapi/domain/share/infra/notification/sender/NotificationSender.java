package com.example.surveyapi.domain.share.infra.notification.sender;

import com.example.surveyapi.domain.share.domain.notification.entity.Notification;

public interface NotificationSender {
	void send(Notification notification);
}
