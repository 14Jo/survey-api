package com.example.surveyapi.domain.share.application.notification;

import java.util.List;

import com.example.surveyapi.domain.share.domain.notification.entity.Notification;

public interface NotificationSendService {
	void send(Notification notifications);
}
