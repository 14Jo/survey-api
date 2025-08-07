package com.example.surveyapi.domain.share.infra.notification;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.example.surveyapi.domain.share.domain.notification.entity.Notification;
import com.example.surveyapi.domain.share.domain.notification.repository.NotificationRepository;
import com.example.surveyapi.domain.share.domain.notification.vo.Status;
import com.example.surveyapi.domain.share.infra.notification.jpa.NotificationJpaRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class NotificationRepositoryImpl implements NotificationRepository {
	private final NotificationJpaRepository notificationJpaRepository;

	@Override
	public Page<Notification> findByShareId(Long shareId, Pageable pageable) {
		return notificationJpaRepository.findByShareId(shareId, pageable);
	}

	@Override
	public void saveAll(List<Notification> notifications) {
		notificationJpaRepository.saveAll(notifications);
	}

	@Override
	public List<Notification> findBeforeSent(Status status, LocalDateTime notifyAt) {
		return notificationJpaRepository.findByStatusAndNotifyAtLessThanEqual(status, notifyAt);
	}

	@Override
	public void save(Notification notification) {
		notificationJpaRepository.save(notification);
	}
}
