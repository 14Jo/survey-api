package com.example.surveyapi.domain.share.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

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
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import com.example.surveyapi.domain.share.application.client.UserEmailDto;
import com.example.surveyapi.domain.share.application.client.UserServicePort;
import com.example.surveyapi.domain.share.application.share.ShareService;
import com.example.surveyapi.domain.share.application.share.dto.ShareResponse;
import com.example.surveyapi.domain.share.domain.notification.entity.Notification;
import com.example.surveyapi.domain.share.domain.notification.repository.NotificationRepository;
import com.example.surveyapi.domain.share.domain.notification.vo.Status;
import com.example.surveyapi.domain.share.domain.share.entity.Share;
import com.example.surveyapi.domain.share.domain.share.repository.ShareRepository;
import com.example.surveyapi.domain.share.domain.notification.vo.ShareMethod;
import com.example.surveyapi.domain.share.domain.share.vo.ShareSourceType;
import com.example.surveyapi.global.exception.CustomErrorCode;
import com.example.surveyapi.global.exception.CustomException;

@Transactional
@SpringBootTest
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
	@MockBean
	private UserServicePort userServicePort;

	private Long savedShareId;
	private static final String AUTH_HEADER = "Bearer test-token";

	@BeforeEach
	void setUp() {
		ShareResponse response = shareService.createShare(
			ShareSourceType.PROJECT_MEMBER,
			1L,
			1L,
			LocalDateTime.of(2025, 12, 31, 23, 59, 59)
		);
		Share savedShare = shareRepository.findBySource(ShareSourceType.PROJECT_MEMBER, 1L);
		savedShareId = savedShare.getId();
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
	@DisplayName("공유 생성 실패 - 이미 존재하는 공유")
	void createShare_duplicate_fail() {
		//given
		Long sourceId = 1L;

		//when, then
		assertThatThrownBy(() -> shareService.createShare(
			ShareSourceType.PROJECT_MEMBER, sourceId,
			1L, LocalDateTime.of(2025, 12, 31, 23, 59, 59)
		)).isInstanceOf(CustomException.class)
			.hasMessageContaining(CustomErrorCode.ALREADY_EXISTED_SHARE.getMessage());
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
		shareService.createNotifications(AUTH_HEADER, savedShareId, creatorId, shareMethod, emails, notifyAt);

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
	@DisplayName("PUSH 알림 생성 성공 - 호출 확인")
	void createNotification_push_success() {
		//given
		Long creatorId = 1L;
		List<String> emails = List.of("test1@test.com", "test2@test.com");
		LocalDateTime notifyAt = LocalDateTime.now();
		ShareMethod shareMethod = ShareMethod.PUSH;

		when(userServicePort.getUserByEmail(eq(AUTH_HEADER), anyString()))
			.thenReturn(new UserEmailDto(100L, "test1@test.com"));

		//when
		shareService.createNotifications(AUTH_HEADER, savedShareId, creatorId, shareMethod, emails, notifyAt);

		//then
		verify(userServicePort, atLeastOnce()).getUserByEmail(eq(AUTH_HEADER), anyString());
		Share share = shareRepository.findById(savedShareId).orElseThrow();
		assertThat(share.getNotifications()).hasSize(2);
	}

	@Test
	@DisplayName("APP 알림 생성")
	void createNotification_APP_success() {
		//given
		Long creatorId = 1L;
		List<String> emails = List.of("test1@test.com", "test2@test.com");
		LocalDateTime notifyAt = LocalDateTime.now();
		ShareMethod shareMethod = ShareMethod.APP;

		when(userServicePort.getUserByEmail(eq(AUTH_HEADER), anyString()))
			.thenReturn(new UserEmailDto(100L, "test1@test.com"));

		//when
		shareService.createNotifications(AUTH_HEADER, savedShareId, creatorId, shareMethod, emails, notifyAt);

		//then
		verify(userServicePort, atLeastOnce()).getUserByEmail(eq(AUTH_HEADER), anyString());
		Share share = shareRepository.findById(savedShareId).orElseThrow();
		assertThat(share.getNotifications()).hasSize(2);
		for (Notification notification : share.getNotifications()) {
			assertThat(notification.getShareMethod()).isEqualTo(ShareMethod.APP);
			assertThat(notification.getNotifyAt()).isEqualTo(notifyAt);
		}
	}

	@Test
	@DisplayName("공유 조회 성공")
	void getShare_success() {
		ShareResponse response = shareService.getShare(savedShareId, 1L);
    
		Share share = shareService.getShareEntity(savedShareId, 1L);
		assertThat(response.getShareLink()).isEqualTo(share.getLink());
	}

	@Test
	@DisplayName("공유 조회 실패 - 권한 없음")
	void getShare_fail() {
		assertThatThrownBy(() -> shareService.getShare(savedShareId, 1234L))
			.isInstanceOf(CustomException.class)
			.hasMessageContaining(CustomErrorCode.NOT_FOUND_SHARE.getMessage());
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

		Share share = shareService.getShareBySource(ShareSourceType.PROJECT_MEMBER, 10L);

		//when
		String result = shareService.delete(share.getId(), 2L);

		//then
		assertThat(result).isEqualTo("공유 삭제 완료");
		assertThat(shareRepository.findById(share.getId())).isEmpty();
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
	@DisplayName("토큰 조회 실패 - 만료")
	void getShareByToken_fail() {
		//given
		ShareResponse expiredShare = shareService.createShare(
			ShareSourceType.PROJECT_MEMBER, 20L, 1L,
			LocalDateTime.now().minusDays(1)
		);

		Share saved = shareRepository.findBySource(ShareSourceType.PROJECT_MEMBER, 20L);
		savedShareId = saved.getId();
		Share share = shareRepository.findById(savedShareId).orElseThrow();

		//when, then
		assertThatThrownBy(() -> shareService.getShareByToken(share.getToken()))
			.isInstanceOf(CustomException.class)
			.hasMessageContaining(CustomErrorCode.SHARE_EXPIRED.getMessage());
	}

	@Test
	@DisplayName("공유 목록 조회")
	void getShareBySource_success() {
		List<Share> shares = shareService.getShareBySourceId(1L);

		assertThat(shares).isNotEmpty();
		assertThat(shares.get(0).getSourceId()).isEqualTo(1L);
	}
}
