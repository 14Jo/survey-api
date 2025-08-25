package com.example.surveyapi.share.domain.notification.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.surveyapi.share.domain.notification.entity.Notification;
import com.example.surveyapi.share.domain.notification.vo.Status;

public interface NotificationRepository {
	Page<Notification> findByShareId(Long shareId, Pageable pageable);

	void saveAll(List<Notification> notifications);

	List<Notification> findBeforeSent(Status status, LocalDateTime notifyAt);

	void save(Notification notification);

	Optional<Notification> findById(Long id);
}
