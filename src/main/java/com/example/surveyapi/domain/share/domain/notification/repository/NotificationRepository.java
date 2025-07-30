package com.example.surveyapi.domain.share.domain.notification.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.surveyapi.domain.share.domain.notification.entity.Notification;

public interface NotificationRepository {
	Page<Notification> findByShareId(Long shareId, Pageable pageable);

	void saveAll(List<Notification> notifications);
}
