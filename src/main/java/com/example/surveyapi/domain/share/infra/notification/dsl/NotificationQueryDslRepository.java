package com.example.surveyapi.domain.share.infra.notification.dsl;

import com.example.surveyapi.domain.share.application.notification.dto.NotificationPageResponse;
import com.querydsl.jpa.impl.JPAQueryFactory;

public interface NotificationQueryDslRepository {
	NotificationPageResponse findByShareId(Long shareId, Long requesterId, int page, int size);
}
