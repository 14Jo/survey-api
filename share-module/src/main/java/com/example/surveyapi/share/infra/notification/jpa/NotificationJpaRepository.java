package com.example.surveyapi.share.infra.notification.jpa;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.surveyapi.share.domain.notification.entity.Notification;
import com.example.surveyapi.share.domain.notification.vo.Status;

public interface NotificationJpaRepository extends JpaRepository<Notification, Long> {
	Page<Notification> findByShareId(Long shareId, Pageable pageable);

	List<Notification> findByStatusAndNotifyAtLessThanEqual(Status status, LocalDateTime notifyAt);
}
