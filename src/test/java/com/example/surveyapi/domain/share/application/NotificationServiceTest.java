package com.example.surveyapi.domain.share.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import com.example.surveyapi.domain.share.application.notification.NotificationService;
import com.example.surveyapi.domain.share.application.notification.dto.NotificationResponse;
import com.example.surveyapi.domain.share.domain.notification.entity.Notification;
import com.example.surveyapi.domain.share.domain.notification.repository.query.NotificationQueryRepository;
import com.example.surveyapi.domain.share.domain.notification.vo.Status;
import com.example.surveyapi.domain.share.domain.share.entity.Share;
import com.example.surveyapi.global.enums.CustomErrorCode;
import com.example.surveyapi.global.exception.CustomException;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {
	@InjectMocks
	private NotificationService notificationService;
	@Mock
	private NotificationQueryRepository notificationQueryRepository;

	@Test
	@DisplayName("알림 이력 조회 - 정상")
	void gets_success() {
		//given
		Long shareId = 1L;
		Long requesterId = 1L;
		int page = 0;
		int size = 10;
		Share mockShare = new Share();

		Notification mockNotification = new Notification();
		ReflectionTestUtils.setField(mockNotification, "id", 1L);
		ReflectionTestUtils.setField(mockNotification, "share", mockShare);
		ReflectionTestUtils.setField(mockNotification, "recipientId", requesterId);
		ReflectionTestUtils.setField(mockNotification, "status", Status.SENT);
		ReflectionTestUtils.setField(mockNotification, "sentAt", LocalDateTime.now());
		ReflectionTestUtils.setField(mockNotification, "failedReason", null);

		Pageable pageable = PageRequest.of(page, size);
		NotificationResponse mockNotificationResponse = NotificationResponse.from(mockNotification);
		Page<NotificationResponse> mockPage = new PageImpl<>(List.of(mockNotificationResponse), pageable, 1);

		given(notificationQueryRepository.findPageByShareId(shareId, requesterId, pageable))
			.willReturn(mockPage);

		//when
		Page<NotificationResponse> response =  notificationService.gets(shareId, requesterId, pageable);

		//then
		assertThat(response).isNotNull();
		assertThat(response.getContent()).hasSize(1);
		assertThat(response.getContent().get(0).getId()).isEqualTo(1L);
		assertThat(response.getTotalPages()).isEqualTo(1);
		assertThat(response.getSize()).isEqualTo(10);
	}

	@Test
	@DisplayName("알림 이력 조회 실패 - 존재하지 않는 공유 ID")
	void gts_failed_invalidShareId() {
		//given
		Long shareId = 999L;
		Long requesterId = 1L;
		Pageable pageable = PageRequest.of(0, 10);

		given(notificationQueryRepository.findPageByShareId(shareId, requesterId, pageable))
			.willThrow(new CustomException(CustomErrorCode.NOT_FOUND_SHARE));

		//when, then
		assertThatThrownBy(() -> notificationService.gets(shareId, requesterId, pageable))
			.isInstanceOf(CustomException.class)
			.hasMessageContaining(CustomErrorCode.NOT_FOUND_SHARE.getMessage());
	}
}
