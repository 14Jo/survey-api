package com.example.surveyapi.domain.share.infra.notification.dsl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.surveyapi.domain.share.application.notification.dto.NotificationResponse;

public interface NotificationQueryDslRepository {
	Page<NotificationResponse> findByShareId(Long shareId, Long requesterId, Pageable pageable);
}
