package com.example.surveyapi.domain.share.application;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import com.example.surveyapi.domain.share.application.share.ShareService;
import com.example.surveyapi.domain.share.application.share.dto.ShareResponse;
import com.example.surveyapi.domain.share.domain.notification.entity.Notification;
import com.example.surveyapi.domain.share.domain.notification.repository.NotificationRepository;
import com.example.surveyapi.domain.share.domain.notification.vo.Status;
import com.example.surveyapi.domain.share.domain.share.entity.Share;
import com.example.surveyapi.domain.share.domain.share.repository.ShareRepository;
import com.example.surveyapi.domain.share.domain.share.vo.ShareMethod;
import com.example.surveyapi.domain.share.domain.share.vo.ShareSourceType;

@Transactional
@ActiveProfiles("test")
@SpringBootTest
@Rollback(value = false)
@TestPropertySource(properties = "management.health.mail.enabled=false")
class ShareServiceTest {
	@Autowired
	private ShareRepository shareRepository;
	@Autowired
	private ShareService shareService;
	@Autowired
	private ApplicationEventPublisher eventPublisher;
	@Autowired
	private NotificationRepository notificationRepository;
	@MockBean
	private JavaMailSender javaMailSender;

	private Long savedShareId;

	@BeforeEach
	void setUp() {
		ShareResponse response = shareService.createShare(
			ShareSourceType.PROJECT_MEMBER,
			1L,
			1L,
			LocalDateTime.of(2025, 12, 31, 23, 59, 59)
		);
		savedShareId = response.getId();
	}

	@Test
	@DisplayName("공유 생성")
	void createShare_success() {
		Share share = shareRepository.findById(savedShareId)
			.orElseThrow();

		assertThat(share.getId()).isEqualTo(savedShareId);
		assertThat(share.getNotifications()).isEmpty();
	}

	@Test
	@DisplayName("이메일 알림 생성")
	void createNotifications_success() {
		//given
		Long creatorId = 1L;
		List<String> emails = List.of("user1@example.com", "user2@example.com");
		LocalDateTime notifyAt = LocalDateTime.now();
		ShareMethod shareMethod= ShareMethod.EMAIL;
		//when
		shareService.createNotifications(savedShareId, creatorId, shareMethod, emails, notifyAt);

		//then
		Share share = shareRepository.findById(savedShareId).orElseThrow();
		List<Notification> notifications = share.getNotifications();

		assertThat(notifications).hasSize(2);
		for(Notification notification : notifications) {
			assertThat(notification.getRecipientEmail()).isIn(emails);
			assertThat(notification.getNotifyAt()).isEqualTo(notifyAt);
			assertThat(notification.getStatus()).isEqualTo(Status.READY_TO_SEND);
		}
	}

	@Test
	@DisplayName("공유 조회 성공")
	void getShare_success() {
		ShareResponse response = shareService.getShare(savedShareId, 1L);

		assertThat(response.getId()).isEqualTo(savedShareId);
		// assertThat(response.getShareMethod()).isEqualTo(ShareMethod.EMAIL); // shareMethod 필드 주석 처리됨
	}

	@Test
	@DisplayName("공유 삭제 성공")
	void delete_success() {
		//given
		ShareResponse response = shareService.createShare(
			ShareSourceType.PROJECT_MEMBER,
			10L,
			2L,
			LocalDateTime.of(2025, 12, 31, 23, 59, 59)
		);

		//when
		String result = shareService.delete(response.getId(), 2L);

		//then
		assertThat(result).isEqualTo("공유 삭제 완료");
		assertThat(shareRepository.findById(response.getId())).isEmpty();
	}

	@Test
	@DisplayName("토큰 조회 성공")
	void getShareByToken_success() {
		//given
		Share share = shareRepository.findById(savedShareId).orElseThrow();
		String token = share.getToken();

		//when
		Share result = shareService.getShareByToken(token);

		//then
		assertThat(result.getId()).isEqualTo(savedShareId);
		assertThat(result.isDeleted()).isFalse();
	}

	@Test
	@DisplayName("공유 목록 조회")
	void getShareBySource_success() {
		List<Share> shares = shareService.getShareBySource(1L);

		assertThat(shares).isNotEmpty();
		assertThat(shares.get(0).getSourceId()).isEqualTo(1L);
	}
}
