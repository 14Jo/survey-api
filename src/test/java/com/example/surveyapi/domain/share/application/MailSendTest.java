package com.example.surveyapi.domain.share.application;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.example.surveyapi.domain.share.application.notification.NotificationService;
import com.example.surveyapi.domain.share.application.share.ShareService;
import com.example.surveyapi.domain.share.application.share.dto.ShareResponse;
import com.example.surveyapi.domain.share.domain.notification.entity.Notification;
import com.example.surveyapi.domain.share.domain.notification.repository.NotificationRepository;
import com.example.surveyapi.domain.share.domain.notification.vo.Status;
import com.example.surveyapi.domain.share.domain.share.entity.Share;
import com.example.surveyapi.domain.share.domain.share.repository.ShareRepository;
import com.example.surveyapi.domain.share.domain.notification.vo.ShareMethod;
import com.example.surveyapi.domain.share.domain.share.vo.ShareSourceType;
import com.example.surveyapi.global.enums.CustomErrorCode;
import com.example.surveyapi.global.exception.CustomException;

@Transactional
@ActiveProfiles("test")
@SpringBootTest
class MailSendTest {
	@Autowired
	private NotificationService notificationService;
	@Autowired
	private NotificationRepository notificationRepository;
	@Autowired
	private ShareService shareService;
	@Autowired
	private ShareRepository shareRepository;

	private Long savedShareId;

	@BeforeEach
	void setUp() {
		ShareResponse response = shareService.createShare(
			ShareSourceType.PROJECT_MEMBER,
			1L,
			1L,
			LocalDateTime.of(2025, 12, 31, 23, 59, 59)
		);
		Share savedShare = shareRepository.findBySource(ShareSourceType.PROJECT_MEMBER, 1L).get(0);
		savedShareId = savedShare.getId();
	}

	@Test
	@DisplayName("MailHog 사용한 이메일 발송")
	void sendEmail_success() {
		//given
		Share share = shareRepository.findById(savedShareId)
			.orElseThrow(() -> new CustomException(CustomErrorCode.NOT_FOUND_SHARE));

		Notification notification = Notification.createForShare(
			share, ShareMethod.EMAIL, 1L, "test@example.com", LocalDateTime.now()
		);

		//when
		notificationService.send(notification);

		//then
		Notification saved = notificationRepository.findById(notification.getId())
			.orElseThrow();
		assertThat(saved.getStatus()).isEqualTo(Status.SENT);
	}
}
