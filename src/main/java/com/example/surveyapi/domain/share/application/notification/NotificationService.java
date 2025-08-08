package com.example.surveyapi.domain.share.application.notification;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.surveyapi.domain.share.application.client.ShareValidationResponse;
import com.example.surveyapi.domain.share.application.notification.dto.NotificationResponse;
import com.example.surveyapi.domain.share.domain.notification.entity.Notification;
import com.example.surveyapi.domain.share.domain.notification.repository.NotificationRepository;
import com.example.surveyapi.domain.share.domain.notification.repository.query.NotificationQueryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {
	private final NotificationQueryRepository notificationQueryRepository;
	private final NotificationRepository notificationRepository;
	private final NotificationSendService notificationSendService;

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

	public Page<NotificationResponse> gets(
		Long shareId,
		Long requesterId,
		Pageable pageable) {
		Page<NotificationResponse> notifications = notificationQueryRepository.findPageByShareId(shareId, requesterId, pageable);
		return notifications;
	}

	public ShareValidationResponse isRecipient(Long sourceId, Long recipientId) {
		boolean valid = notificationQueryRepository.isRecipient(sourceId, recipientId);

		return new ShareValidationResponse(valid);
	}
}
