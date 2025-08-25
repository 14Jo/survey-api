package com.example.surveyapi.share.infra.notification.dsl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.surveyapi.share.domain.notification.entity.Notification;

public interface NotificationQueryDslRepository {
	Page<Notification> findByShareId(Long shareId, Long requesterId, Pageable pageable);

	boolean isRecipient(Long sourceId, Long recipientId);

	Page<Notification> findByUserId(Long userId, Pageable pageable);
}
