package com.example.surveyapi.domain.share.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.example.surveyapi.domain.share.application.notification.NotificationService;
import com.example.surveyapi.domain.share.application.share.ShareService;
import com.example.surveyapi.domain.share.application.share.dto.ShareResponse;
import com.example.surveyapi.domain.share.domain.fcm.entity.FcmToken;
import com.example.surveyapi.domain.share.domain.fcm.repository.FcmTokenRepository;
import com.example.surveyapi.domain.share.domain.notification.entity.Notification;
import com.example.surveyapi.domain.share.domain.notification.repository.NotificationRepository;
import com.example.surveyapi.domain.share.domain.notification.vo.Status;
import com.example.surveyapi.domain.share.domain.share.entity.Share;
import com.example.surveyapi.domain.share.domain.share.vo.ShareMethod;
import com.example.surveyapi.domain.share.domain.share.vo.ShareSourceType;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;

@Transactional
@ActiveProfiles("test")
@SpringBootTest
public class PushSendTest {
	@Autowired
	private ShareService shareService;
	@Autowired
	private NotificationRepository notificationRepository;
	@Autowired
	private NotificationService notificationService;
	@MockBean
	private FcmTokenRepository fcmTokenRepository;
	@MockBean
	private FirebaseMessaging firebaseMessaging;

	private Long shareId;

	@BeforeEach
	void setUp() {
		ShareResponse response = shareService.createShare(
			ShareSourceType.PROJECT_MEMBER,
			1L,
			1L,
			LocalDateTime.of(2025, 12, 31, 23, 59, 59)
		);
		shareId = response.getId();
	}

	@Test
	@DisplayName("PUSH send test success")
	void sendPushTest_success() throws Exception {
		//given
		Share share = shareService.getShareEntity(shareId, 1L);

		Notification notification = Notification.createForShare(
			share,
			ShareMethod.PUSH,
			1L,
			"test@test.com",
			LocalDateTime.now()
		);

		when(fcmTokenRepository.findByUserId(1L))
			.thenReturn(Optional.of(new FcmToken(1L, "mock-token")));

		when(firebaseMessaging.send(any(Message.class)))
			.thenAnswer(invocation -> "mock-response");

		//when
		notificationService.send(notification);

		Notification saved = notificationRepository.findById(notification.getId())
			.orElseThrow();

		//then
		assertThat(saved.getStatus()).isEqualTo(Status.SENT);

		verify(fcmTokenRepository, atLeastOnce()).findByUserId(1L);
	}
}
