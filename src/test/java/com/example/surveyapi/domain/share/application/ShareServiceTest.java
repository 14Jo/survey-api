package com.example.surveyapi.domain.share.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.util.ReflectionTestUtils;

import com.example.surveyapi.domain.share.application.share.ShareService;
import com.example.surveyapi.domain.share.application.share.dto.ShareResponse;
import com.example.surveyapi.domain.share.domain.share.ShareDomainService;
import com.example.surveyapi.domain.share.domain.share.entity.Share;
import com.example.surveyapi.domain.share.domain.share.event.ShareCreateEvent;
import com.example.surveyapi.domain.share.domain.share.repository.ShareRepository;
import com.example.surveyapi.domain.share.domain.share.vo.ShareMethod;

@ExtendWith(MockitoExtension.class)
class ShareServiceTest {
	@Mock
	private ShareRepository shareRepository;
	@Mock
	private ShareDomainService shareDomainService;
	@Mock
	private ApplicationEventPublisher eventPublisher;
	@InjectMocks
	private ShareService shareService;

	@Test
	@DisplayName("공유 생성 - 정상 저장")
	void createShare_success() {
		//given
		Long surveyId = 1L;
		String shareLink = "https://example.com/share/12345";

		Share share = new Share(surveyId, ShareMethod.URL, shareLink);
		ReflectionTestUtils.setField(share, "id", 1L);
		ReflectionTestUtils.setField(share, "createdAt", LocalDateTime.now());
		ReflectionTestUtils.setField(share, "updatedAt", LocalDateTime.now());

		given(shareDomainService.createShare(surveyId, ShareMethod.URL)).willReturn(share);
		given(shareRepository.save(share)).willReturn(share);

		//when
		ShareResponse response = shareService.createShare(surveyId);

		//then
		assertThat(response.getId()).isEqualTo(1L);
		assertThat(response.getSurveyId()).isEqualTo(surveyId);
		assertThat(response.getShareMethod()).isEqualTo(ShareMethod.URL);
		assertThat(response.getShareLink()).isEqualTo(shareLink);
		assertThat(response.getCreatedAt()).isNotNull();
		assertThat(response.getUpdatedAt()).isNotNull();

		verify(eventPublisher, times(1))
			.publishEvent(any(ShareCreateEvent.class));

		verify(shareRepository).save(share);
	}
}
