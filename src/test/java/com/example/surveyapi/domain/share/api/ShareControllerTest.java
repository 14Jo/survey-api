package com.example.surveyapi.domain.share.api;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import com.example.surveyapi.domain.share.api.share.ShareController;
import com.example.surveyapi.domain.share.application.share.ShareService;
import com.example.surveyapi.domain.share.application.share.dto.ShareResponse;
import com.example.surveyapi.domain.share.domain.share.entity.Share;
import com.example.surveyapi.domain.share.domain.share.vo.ShareMethod;

@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(properties = "SECRET_KEY=123456789012345678901234567890")
@WebMvcTest(ShareController.class)
class ShareControllerTest {
	@Autowired
	private MockMvc mockMvc;
	@MockBean
	private ShareService shareService;

	private final String URI = "/api/v1/share-tasks";

	@Test
	@DisplayName("공유 생성 api - 정상 요청, 201 return")
	void createShare_success() throws Exception {
		//given
		Long surveyId = 1L;
		ShareMethod shareMethod = ShareMethod.URL;
		String shareLink = "https://example.com/share/12345";

		String requestJson = """
			{
				\"surveyId\": 1
			}
			""";
		Share shareMock = new Share(surveyId, shareMethod, shareLink);

		ReflectionTestUtils.setField(shareMock, "id", 1L);
		ReflectionTestUtils.setField(shareMock, "createdAt", LocalDateTime.now());
		ReflectionTestUtils.setField(shareMock, "updatedAt", LocalDateTime.now());

		ShareResponse mockResponse = ShareResponse.from(shareMock);
		given(shareService.createShare(eq(surveyId))).willReturn(mockResponse);

		//when, then
		mockMvc.perform(post(URI)
			.contentType(MediaType.APPLICATION_JSON)
			.content(requestJson))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.success").value(true))
			.andExpect(jsonPath("$.data.surveyId").value(1))
			.andExpect(jsonPath("$.data.shareMethod").value("URL"))
			.andExpect(jsonPath("$.data.shareLink").value("https://example.com/share/12345"))
			.andExpect(jsonPath("$.data.createdAt").exists())
			.andExpect(jsonPath("$.data.updatedAt").exists());
	}

	@Test
	@DisplayName("공유 생성 api - surveyId 누락(요청 body 누락), 400 return")
	void createShare_fail_noSurveyId() throws Exception {
		//given
		String requestJson = "{}";

		//when, then
		mockMvc.perform(post(URI)
			.contentType(MediaType.APPLICATION_JSON)
			.content(requestJson))
			.andExpect(status().isBadRequest());
	}
}
