package com.example.surveyapi.domain.share.application.notification;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.surveyapi.domain.share.application.client.ShareServicePort;
import com.example.surveyapi.domain.share.application.notification.dto.NotificationPageResponse;
import com.example.surveyapi.domain.share.domain.notification.entity.Notification;
import com.example.surveyapi.domain.share.domain.notification.repository.NotificationRepository;
import com.example.surveyapi.domain.share.domain.notification.repository.query.NotificationQueryRepository;
import com.example.surveyapi.domain.share.domain.share.entity.Share;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {
	private final NotificationQueryRepository notificationQueryRepository;
	private final NotificationRepository notificationRepository;
	private final ShareServicePort shareServicePort;

	@Transactional
	public void create(Share share, Long creatorId) {
		List<Long> recipientIds = shareServicePort.getRecipientIds(share.getId(), creatorId);

		List<Notification> notifications = recipientIds.stream()
			.map(recipientId -> Notification.createForShare(share, recipientId))
			.toList();

		notificationRepository.saveAll(notifications);
	}

	public NotificationPageResponse gets(Long shareId, Long requesterId, int page, int size) {
		return notificationQueryRepository.findPageByShareId(shareId, requesterId, page, size);
	}

	private boolean isAdmin(Long userId) {
		//TODO : 관리자 권한 조회 기능, 접근 권한 확인 기능 구현 시 동시에 구현 및 사용

		return false;
	}
}
