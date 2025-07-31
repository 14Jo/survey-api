package com.example.surveyapi.domain.share.application.event;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.example.surveyapi.domain.share.application.notification.NotificationService;
import com.example.surveyapi.domain.share.domain.share.event.ShareCreateEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ShareEventListener {
	private final NotificationService notificationService;

	@EventListener
	public void handleShareCreated(ShareCreateEvent event) {
		log.info("알림 생성 이벤트 수신: shareId = {}", event.getShare().getId());

		try {
			notificationService.create(event.getShare(), event.getCreatorId());
		} catch (Exception e) {
			log.error("알림 생성 중 오류 발생", e);
		}
	}
}
