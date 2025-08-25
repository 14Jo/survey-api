package com.example.surveyapi.share.infra.notification.query;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.example.surveyapi.share.domain.notification.entity.Notification;
import com.example.surveyapi.share.domain.notification.repository.query.NotificationQueryRepository;
import com.example.surveyapi.share.infra.notification.dsl.NotificationQueryDslRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class NotificationQueryRepositoryImpl implements NotificationQueryRepository {
	private final NotificationQueryDslRepository dslRepository;

	@Override
	public Page<Notification> findPageByShareId(Long shareId, Long requesterId, Pageable pageable) {

		return dslRepository.findByShareId(shareId, requesterId, pageable);
	}

	@Override
	public boolean isRecipient(Long sourceId, Long recipientId) {

		return dslRepository.isRecipient(sourceId, recipientId);
	}

	@Override
	public Page<Notification> findPageByUserId(Long userId, Pageable pageable) {

		return dslRepository.findByUserId(userId, pageable);
	}
}
