package com.example.surveyapi.domain.share.domain.notification.repository.query;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.surveyapi.domain.share.application.notification.dto.NotificationResponse;

public interface NotificationQueryRepository {
	Page<NotificationResponse> findPageByShareId(Long shareId, Long requesterId, Pageable pageable);

	boolean isRecipient(Long sourceId, Long recipientId);
}
