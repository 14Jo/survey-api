package com.example.surveyapi.domain.share.application.notification;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.surveyapi.domain.share.application.client.ShareServicePort;
import com.example.surveyapi.domain.share.application.notification.dto.NotificationResponse;
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
	private final NotificationSendService notificationSendService;

	@Transactional
	public void create(Share share, Long creatorId, LocalDateTime notifyAt) {
		List<Long> recipientIds = shareServicePort.getRecipientIds(share.getId(), creatorId);

		List<Notification> notifications = recipientIds.stream()
			.map(recipientId -> Notification.createForShare(share, recipientId, notifyAt))
			.toList();

		notificationRepository.saveAll(notifications);
	}

	@Transactional
	public void send(Notification notification) {
		try {
			notificationSendService.send(notification);
			notification.setSent();
		} catch (Exception e) {
			notification.setFailed(e.getMessage());
		}

		notificationRepository.save(notification);
	}

	public Page<NotificationResponse> gets(Long shareId, Long requesterId, Pageable pageable) {
		Page<NotificationResponse> notifications = notificationQueryRepository.findPageByShareId(shareId, requesterId, pageable);
		return notifications;
	}

	private boolean isAdmin(Long userId) {
		//TODO : 관리자 권한 조회 기능, 접근 권한 확인 기능 구현 시 동시에 구현 및 사용

		return false;
	}
}
