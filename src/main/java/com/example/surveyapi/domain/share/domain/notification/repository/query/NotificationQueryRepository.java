package com.example.surveyapi.domain.share.domain.notification.repository.query;

import com.example.surveyapi.domain.share.application.notification.dto.NotificationPageResponse;

public interface NotificationQueryRepository {
	NotificationPageResponse findPageByShareId(Long shareId, Long requesterId, int page, int size);
}
