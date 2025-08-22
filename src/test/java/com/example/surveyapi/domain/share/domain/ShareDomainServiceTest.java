package com.example.surveyapi.domain.share.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.surveyapi.domain.share.domain.share.ShareDomainService;
import com.example.surveyapi.domain.share.domain.share.entity.Share;
import com.example.surveyapi.domain.share.domain.share.vo.ShareSourceType;
import com.example.surveyapi.global.exception.CustomErrorCode;
import com.example.surveyapi.global.exception.CustomException;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;

@ExtendWith(MockitoExtension.class)
class ShareDomainServiceTest {
	private ShareDomainService shareDomainService;

	@BeforeEach
	void setUp() {
		shareDomainService = new ShareDomainService();
	}

	@Test
	@DisplayName("공유 링크 생성 - 설문 링크 정상 생성")
	void createShare_success_survey() {
		//given
		Long sourceId = 1L;
		Long creatorId = 1L;
		ShareSourceType sourceType = ShareSourceType.SURVEY;
		LocalDateTime expirationDate = LocalDateTime.of(2025, 12, 31, 23, 59, 59);

		//when
		Share share = shareDomainService.createShare(
			sourceType, sourceId, creatorId, expirationDate);

		//then
		assertThat(share).isNotNull();
		assertThat(share.getSourceType()).isEqualTo(sourceType);
		assertThat(share.getSourceId()).isEqualTo(sourceId);
		assertThat(share.getLink()).startsWith("https://localhost:8080/api/v2/share/surveys/");
		assertThat(share.getLink().length()).isGreaterThan("https://localhost:8080/api/v2/share/surveys/".length());
	}

	@Test
	@DisplayName("공유 링크 생성 - 프로젝트 링크 정상 생성")
	void createShare_success_project() {
		//given
		Long sourceId = 1L;
		Long creatorId = 1L;
		ShareSourceType sourceType = ShareSourceType.PROJECT_MEMBER;
		LocalDateTime expirationDate = LocalDateTime.of(2025, 12, 31, 23, 59, 59);

		//when
		Share share = shareDomainService.createShare(
			sourceType, sourceId, creatorId, expirationDate);

		//then
		assertThat(share).isNotNull();
		assertThat(share.getSourceType()).isEqualTo(sourceType);
		assertThat(share.getSourceId()).isEqualTo(sourceId);
		assertThat(share.getLink()).startsWith("https://localhost:8080/api/v2/share/projects/");
		assertThat(share.getLink().length()).isGreaterThan("https://localhost:8080/api/v2/share/projects/".length());
	}

	@Test
	@DisplayName("Redirect URL 생성 - 설문")
	void redirectUrl_survey() {
		//given
		LocalDateTime expirationDate = LocalDateTime.of(2025, 12, 31, 23, 59, 59);

		Share share = new Share(
			ShareSourceType.SURVEY, 1L, 1L, "token", "link", expirationDate);

		//when, then
		String url = shareDomainService.getRedirectUrl(share);

		assertThat(url).isEqualTo("api/v1/survey/1/detail");
	}

	@Test
	@DisplayName("Redirect URL 생성 - 프로젝트 멤버")
	void redirectUrl_projectMember() {
		//given
		LocalDateTime expirationDate = LocalDateTime.of(2025, 12, 31, 23, 59, 59);

		Share share = new Share(
			ShareSourceType.PROJECT_MEMBER, 1L, 1L, "token", "link", expirationDate);

		//when, then
		String url = shareDomainService.getRedirectUrl(share);

		assertThat(url).isEqualTo("/api/projects/members/1");
	}

	@Test
	@DisplayName("Redirect URL 생성 - 프로젝트 매니저")
	void redirectUrl_projectManager() {
		//given
		LocalDateTime expirationDate = LocalDateTime.of(2025, 12, 31, 23, 59, 59);

		Share share = new Share(
			ShareSourceType.PROJECT_MANAGER, 1L, 1L, "token", "link", expirationDate);

		//when, then
		String url = shareDomainService.getRedirectUrl(share);

		assertThat(url).isEqualTo("/api/projects/managers/1");
	}

	@Test
	@DisplayName("링크 생성 실패 - 지원하지 않는 공유 타입")
	void generateLink_fail_invalidType() {
		assertThatThrownBy(() -> shareDomainService.generateLink(null, "token"))
			.isInstanceOf(CustomException.class)
			.hasMessageContaining(CustomErrorCode.UNSUPPORTED_SHARE_METHOD.getMessage());
	}
}
