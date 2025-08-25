package com.example.surveyapi.share.application.notification;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.surveyapi.share.domain.notification.entity.Notification;
import com.example.surveyapi.share.domain.notification.repository.NotificationRepository;
import com.example.surveyapi.share.domain.notification.vo.Status;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationScheduler {
	private final NotificationRepository notificationRepository;
	private final NotificationService notificationService;

	@Scheduled(fixedDelay = 60000)
	@Transactional
	public void send() {
		LocalDateTime now = LocalDateTime.now();

		List<Notification> toSend = notificationRepository.findBeforeSent(
			Status.READY_TO_SEND, now
		);

		toSend.forEach(notificationService::send);
	}
}