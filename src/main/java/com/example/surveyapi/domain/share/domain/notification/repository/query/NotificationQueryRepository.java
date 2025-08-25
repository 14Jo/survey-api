package com.example.surveyapi.domain.share.domain.notification.repository.query;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.surveyapi.domain.share.domain.notification.entity.Notification;

public interface NotificationQueryRepository {
	Page<Notification> findPageByShareId(Long shareId, Long requesterId, Pageable pageable);

	boolean isRecipient(Long sourceId, Long recipientId);

	Page<Notification> findPageByUserId(Long userId, Pageable pageable);
}
