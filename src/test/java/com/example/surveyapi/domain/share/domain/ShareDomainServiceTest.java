package com.example.surveyapi.domain.share.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.surveyapi.domain.share.domain.share.ShareDomainService;
import com.example.surveyapi.domain.share.domain.share.entity.Share;
import com.example.surveyapi.domain.share.domain.share.vo.ShareMethod;
import com.example.surveyapi.global.enums.CustomErrorCode;
import com.example.surveyapi.global.exception.CustomException;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class ShareDomainServiceTest {
	private ShareDomainService shareDomainService;

	@BeforeEach
	void setUp() {
		shareDomainService = new ShareDomainService();
	}

	@Test
	@DisplayName("공유 url 생성 - BASE_URL + UUID 링크 정상 생성")
	void createShare_success_url() {
		//given
		Long surveyId = 1L;
		Long creatorId = 1L;
		ShareMethod shareMethod = ShareMethod.URL;
		List<Long> recipientIds = List.of(2L, 3L, 4L);

		//when
		Share share = shareDomainService.createShare(surveyId, creatorId, shareMethod, recipientIds);

		//then
		assertThat(share).isNotNull();
		assertThat(share.getSurveyId()).isEqualTo(surveyId);
		assertThat(share.getShareMethod()).isEqualTo(shareMethod);
		assertThat(share.getLink()).startsWith("https://everysurvey.com/surveys/share/");
		assertThat(share.getLink().length()).isGreaterThan("https://everysurvey.com/surveys/share/".length());
	}

	@Test
	@DisplayName("generateLink - UUID 기반 공유 링크 정상 생성")
	void generateLink_success_url() {
		//given
		ShareMethod shareMethod = ShareMethod.URL;

		//when
		String link = shareDomainService.generateLink(shareMethod);

		//then
		assertThat(link).startsWith("https://everysurvey.com/surveys/share/");
		String token = link.replace("https://everysurvey.com/surveys/share/", "");
		assertThat(token).matches("^[a-fA-F0-9]{32}$");
	}

	@Test
	@DisplayName("공유 email 생성 - 정상 생성")
	void createShare_success_email() {
		//given
		Long surveyId = 1L;
		Long creatorId = 1L;
		ShareMethod shareMethod = ShareMethod.EMAIL;
		List<Long> recipientIds = List.of(2L, 3L, 4L);

		//when
		Share share = shareDomainService.createShare(surveyId, creatorId, shareMethod, recipientIds);

		//then
		assertThat(share).isNotNull();
		assertThat(share.getSurveyId()).isEqualTo(surveyId);
		assertThat(share.getShareMethod()).isEqualTo(shareMethod);
		assertThat(share.getLink()).startsWith("email://");
		assertThat(share.getLink().length()).isGreaterThan("email://".length());
	}

	@Test
	@DisplayName("generateLink - 이메일 정상 생성")
	void generateLink_success_email() {
		//given
		ShareMethod shareMethod = ShareMethod.EMAIL;

		//when
		String link = shareDomainService.generateLink(shareMethod);

		//then
		assertThat(link).startsWith("email://");
		String token = link.replace("email://", "");
		assertThat(token).matches("^[a-fA-F0-9]{32}$");
	}

	@Test
	@DisplayName("generateLink - 지원하지 않는 공유 방식 예외")
	void generateLink_failed_invalidMethod() {
		//given
		ShareMethod shareMethod = null;

		//when, then
		assertThatThrownBy(() -> shareDomainService.generateLink(shareMethod))
			.isInstanceOf(CustomException.class)
			.hasMessageContaining(CustomErrorCode.UNSUPPORTED_SHARE_METHOD.getMessage());
	}
}
