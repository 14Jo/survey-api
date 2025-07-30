package com.example.surveyapi.domain.share.application;

import static org.assertj.core.api.Assertions.*;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import com.example.surveyapi.domain.share.application.share.ShareService;
import com.example.surveyapi.domain.share.application.share.dto.ShareResponse;
import com.example.surveyapi.domain.share.domain.share.entity.Share;
import com.example.surveyapi.domain.share.domain.share.repository.ShareRepository;
import com.example.surveyapi.domain.share.domain.share.vo.ShareMethod;
import com.example.surveyapi.global.enums.CustomErrorCode;
import com.example.surveyapi.global.exception.CustomException;

@Transactional
@SpringBootTest
class ShareServiceTest {
	@Autowired
	private ShareRepository shareRepository;
	@Autowired
	private ShareService shareService;

	@Test
	@DisplayName("공유 생성 - 정상 저장")
	void createShare_success() {
		//given
		Long surveyId = 1L;
		Long creatorId = 1L;
		ShareMethod shareMethod = ShareMethod.URL;

		//when
		ShareResponse response = shareService.createShare(surveyId, creatorId, shareMethod);

		//then
		assertThat(response.getId()).isNotNull();
		assertThat(response.getSurveyId()).isEqualTo(surveyId);
		assertThat(response.getCreatorId()).isEqualTo(creatorId);
		assertThat(response.getShareMethod()).isEqualTo(ShareMethod.URL);
		assertThat(response.getShareLink()).startsWith("https://everysurvey.com/surveys/share/");
		assertThat(response.getCreatedAt()).isNotNull();
		assertThat(response.getUpdatedAt()).isNotNull();

		Optional<Share> saved = shareRepository.findById(response.getId());
		assertThat(saved).isPresent();
		assertThat(saved.get().getCreatorId()).isEqualTo(creatorId);
	}

	@Test
	@DisplayName("공유 조회 - 조회 성공")
	void getShare_success() {
		//given
		Long surveyId = 1L;
		Long creatorId = 1L;
		ShareResponse response = shareService.createShare(surveyId, creatorId, ShareMethod.URL);

		//when
		ShareResponse result = shareService.getShare(response.getId(), creatorId);

		//then
		assertThat(result).isNotNull();
		assertThat(result.getId()).isEqualTo(response.getId());
		assertThat(result.getSurveyId()).isEqualTo(surveyId);
	}

	@Test
	@DisplayName("공유 조회 - 작성자 불일치 실패")
	void getShare_failed_notCreator() {
		//given
		Long surveyId = 1L;
		Long creatorId = 1L;
		ShareResponse response = shareService.createShare(surveyId, creatorId, ShareMethod.URL);

		//when, then
		assertThatThrownBy(() -> shareService.getShare(response.getId(), 123L))
			.isInstanceOf(CustomException.class)
			.hasMessageContaining(CustomErrorCode.NOT_FOUND_SHARE.getMessage());
	}
}
