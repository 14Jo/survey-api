package com.example.surveyapi.domain.share.application;

import static org.assertj.core.api.Assertions.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.annotation.Commit;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.shaded.org.awaitility.Awaitility;

import com.example.surveyapi.domain.share.application.share.ShareService;
import com.example.surveyapi.domain.share.application.share.dto.ShareResponse;
import com.example.surveyapi.domain.share.domain.notification.entity.Notification;
import com.example.surveyapi.domain.share.domain.notification.vo.Status;
import com.example.surveyapi.domain.share.domain.share.entity.Share;
import com.example.surveyapi.domain.share.domain.share.repository.ShareRepository;
import com.example.surveyapi.domain.share.domain.share.vo.ShareMethod;
import com.example.surveyapi.domain.share.domain.share.vo.ShareSourceType;
import com.example.surveyapi.global.enums.CustomErrorCode;
import com.example.surveyapi.global.event.project.ProjectMemberAddedEvent;
import com.example.surveyapi.global.exception.CustomException;


@Transactional
@ActiveProfiles("test")
@SpringBootTest
@Rollback(value = false)
class ShareServiceTest {
	@Autowired
	private ShareRepository shareRepository;
	@Autowired
	private ShareService shareService;
	@Autowired
	private ApplicationEventPublisher eventPublisher;

	@Test
	@Commit
	@DisplayName("이벤트 기반 공유 생성 - ProjectMember")
	void createShare_success() {
		//given
		Long sourceId = 1L;
		Long creatorId = 1L;
		ShareSourceType sourceType = ShareSourceType.PROJECT_MEMBER;
		LocalDateTime expirationDate = LocalDateTime.of(2025, 12, 31, 23, 59, 59);
		ShareMethod shareMethod = ShareMethod.URL;

		ProjectMemberAddedEvent event = new ProjectMemberAddedEvent(
			sourceId,
			creatorId,
			2L,
			expirationDate
		);

		//when
		eventPublisher.publishEvent(event);

		//then
		Awaitility.await()
			.atMost(Duration.ofSeconds(5))
			.pollInterval(Duration.ofMillis(300))
			.untilAsserted(() -> {
				List<Share> shares = shareRepository.findBySource(sourceId);
				assertThat(shares).isNotEmpty();

				Share share = shares.get(0);
				assertThat(share.getSourceType()).isEqualTo(ShareSourceType.PROJECT_MEMBER);
				assertThat(share.getSourceId()).isEqualTo(sourceId);
				assertThat(share.getCreatorId()).isEqualTo(creatorId);
				assertThat(share.getShareMethod()).isEqualTo(shareMethod);
				assertThat(share.getLink()).startsWith("https://localhost:8080/api/v2/share/projects/");
			});
	}

	@Test
	@DisplayName("공유 조회 - 조회 성공")
	void getShare_success() {
		//given
		Long sourceId = 1L;
		Long creatorId = 1L;
		ShareSourceType sourceType = ShareSourceType.PROJECT_MEMBER;
		LocalDateTime expirationDate = LocalDateTime.of(2025, 12, 31, 23, 59, 59);
		List<Long> recipientIds = List.of(2L, 3L, 4L);
		ShareMethod shareMethod = ShareMethod.URL;
		LocalDateTime notifyAt = LocalDateTime.now();

		ShareResponse response = shareService.createShare(
			sourceType, sourceId, creatorId, shareMethod, expirationDate, recipientIds, notifyAt);

		//when
		ShareResponse result = shareService.getShare(response.getId(), creatorId);

		//then
		assertThat(result).isNotNull();
		assertThat(result.getId()).isEqualTo(response.getId());
		assertThat(result.getSourceType()).isEqualTo(sourceType);
		assertThat(result.getSourceId()).isEqualTo(sourceId);
		assertThat(result.getShareMethod()).isEqualTo(shareMethod);
	}

	@Test
	@DisplayName("공유 조회 - 작성자 불일치 실패")
	void getShare_failed_notCreator() {
		//given
		Long sourceId = 2L;
		Long creatorId = 2L;
		ShareSourceType sourceType = ShareSourceType.PROJECT_MEMBER;
		LocalDateTime expirationDate = LocalDateTime.of(2025, 12, 31, 23, 59, 59);
		List<Long> recipientIds = List.of(2L, 3L, 4L);
		ShareMethod shareMethod = ShareMethod.URL;
		LocalDateTime notifyAt = LocalDateTime.now();

		ShareResponse response = shareService.createShare(
			sourceType, sourceId, creatorId, shareMethod, expirationDate, recipientIds, notifyAt);

		//when, then
		assertThatThrownBy(() -> shareService.getShare(response.getId(), 123L))
			.isInstanceOf(CustomException.class)
			.hasMessageContaining(CustomErrorCode.NOT_FOUND_SHARE.getMessage());
	}
}
