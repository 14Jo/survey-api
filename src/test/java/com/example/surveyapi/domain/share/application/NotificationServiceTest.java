package com.example.surveyapi.domain.share.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

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

import com.example.surveyapi.domain.share.application.notification.NotificationService;
import com.example.surveyapi.domain.share.application.notification.dto.NotificationPageResponse;
import com.example.surveyapi.domain.share.domain.notification.entity.Notification;
import com.example.surveyapi.domain.share.domain.notification.repository.NotificationRepository;
import com.example.surveyapi.domain.share.domain.share.entity.Share;
import com.example.surveyapi.domain.share.domain.share.repository.ShareRepository;
import com.example.surveyapi.global.enums.CustomErrorCode;
import com.example.surveyapi.global.exception.CustomException;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {
	@InjectMocks
	private NotificationService notificationService;
	@Mock
	private NotificationRepository notificationRepository;
	@Mock
	private ShareRepository shareRepository;

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
		Page<Notification> mockPage = new PageImpl<>(List.of(mockNotification), PageRequest.of(page, size), 1);

		given(shareRepository.findById(shareId)).willReturn(Optional.of(mockShare));
		given(notificationRepository.findByShareId(eq(shareId), any(Pageable.class))).willReturn(mockPage);

		//when
		NotificationPageResponse response =  notificationService.gets(shareId, requesterId, page, size);

		//then
		assertThat(response).isNotNull();
		assertThat(response.getContent()).hasSize(1);
		assertThat(response.getPageInfo().getTotalPages()).isEqualTo(1);
		assertThat(response.getPageInfo().getSize()).isEqualTo(10);
	}

	@Test
	@DisplayName("알림 이력 조회 실패 - 존재하지 않는 공유 ID")
	void gts_failed_invalidShareId() {
		//given
		Long shareId = 999L;
		Long requesterId = 1L;

		given(shareRepository.findById(shareId)).willReturn(Optional.empty());

		//when, then
		assertThatThrownBy(() -> notificationService.gets(shareId, requesterId, 0, 10))
			.isInstanceOf(CustomException.class)
			.hasMessageContaining(CustomErrorCode.NOT_FOUND_SHARE.getMessage());
	}
}
