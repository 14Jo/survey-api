package com.example.surveyapi.domain.share.infra.notification.query;

import org.springframework.stereotype.Repository;

import com.example.surveyapi.domain.share.application.notification.dto.NotificationPageResponse;
import com.example.surveyapi.domain.share.domain.notification.entity.QNotification;
import com.example.surveyapi.domain.share.domain.notification.repository.query.NotificationQueryRepository;
import com.example.surveyapi.domain.share.infra.notification.dsl.NotificationQueryDslRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class NotificationQueryRepositoryImpl implements NotificationQueryRepository {
	private final NotificationQueryDslRepository dslRepository;

	@Override
	public NotificationPageResponse findPageByShareId(Long shareId, Long requesterId, int page, int size) {

		return dslRepository.findByShareId(shareId, requesterId, page, size);
	}
}
