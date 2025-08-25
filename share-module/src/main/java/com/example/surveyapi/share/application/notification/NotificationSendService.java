package com.example.surveyapi.share.application.notification;

import com.example.surveyapi.share.domain.notification.entity.Notification;

public interface NotificationSendService {
	void send(Notification notification);
}
