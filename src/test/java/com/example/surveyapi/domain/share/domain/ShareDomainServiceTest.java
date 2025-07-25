package com.example.surveyapi.domain.share.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.surveyapi.domain.share.domain.share.ShareDomainService;
import com.example.surveyapi.domain.share.domain.share.entity.Share;
import com.example.surveyapi.domain.share.domain.share.vo.ShareMethod;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ShareDomainServiceTest {
	private ShareDomainService shareDomainService;

	@BeforeEach
	void setUp() {
		shareDomainService = new ShareDomainService();
	}

	@Test
	@DisplayName("공유 url 생성 - BASE_URL + UUID 링크 정상 생성")
	void createShare_success() {
		//given
		Long surveyId = 1L;
		ShareMethod shareMethod = ShareMethod.URL;

		//when
		Share share = shareDomainService.createShare(surveyId, shareMethod);

		//then
		assertThat(share).isNotNull();
		assertThat(share.getSurveyId()).isEqualTo(surveyId);
		assertThat(share.getShareMethod()).isEqualTo(shareMethod);
		assertThat(share.getLink()).startsWith("https://everysurvey.com/surveys/share/");
		assertThat(share.getLink().length()).isGreaterThan("https://everysurvey.com/surveys/share/".length());
	}

	@Test
	@DisplayName("generateLink - UUID 기반 공유 링크 정상 생성")
	void generateLink_success() {
		//when
		String link = shareDomainService.generateLink();

		//then
		assertThat(link).startsWith("https://everysurvey.com/surveys/share/");
		String token = link.replace("https://everysurvey.com/surveys/share/", "");
		assertThat(token).matches("^[a-fA-F0-9]{32}$");
	}
}
