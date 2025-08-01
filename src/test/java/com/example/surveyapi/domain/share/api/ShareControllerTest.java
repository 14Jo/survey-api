package com.example.surveyapi.domain.share.api;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import com.example.surveyapi.domain.share.application.notification.NotificationService;
import com.example.surveyapi.domain.share.application.notification.dto.NotificationPageResponse;
import com.example.surveyapi.domain.share.application.notification.dto.NotificationResponse;
import com.example.surveyapi.domain.share.application.share.ShareService;
import com.example.surveyapi.domain.share.application.share.dto.ShareResponse;
import com.example.surveyapi.domain.share.domain.notification.vo.Status;
import com.example.surveyapi.domain.share.domain.share.entity.Share;
import com.example.surveyapi.domain.share.domain.notification.vo.ShareMethod;
import com.example.surveyapi.global.enums.CustomErrorCode;
import com.example.surveyapi.global.exception.CustomException;
import com.example.surveyapi.global.util.PageInfo;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(ShareController.class)
class ShareControllerTest {
	@Autowired
	private MockMvc mockMvc;
	@MockBean
	private ShareService shareService;
	@MockBean
	private NotificationService notificationService;

	private final String URI = "/api/v1/share-tasks";

	@BeforeEach
	void setUp() {
		TestingAuthenticationToken auth =
			new TestingAuthenticationToken(1L, null, "ROLE_USER");
		auth.setAuthenticated(true);
		SecurityContextHolder.getContext().setAuthentication(auth);
	}

	@Test
	@DisplayName("공유 생성 api - url 정상 요청, 201 return")
	void createShare_success_url() throws Exception {
		//given
		Long surveyId = 1L;
		Long creatorId = 1L;
		ShareMethod shareMethod = ShareMethod.URL;
		String shareLink = "https://example.com/share/12345";
		List<Long> recipientIds = List.of(2L, 3L, 4L);

		String requestJson = """
			{
				\"surveyId\": 1,
				\"creatorId\": 1,
				\"shareMethod\": \"URL\"
			}
			""";
		Share shareMock = new Share(surveyId, creatorId, shareMethod, shareLink, recipientIds);

		ReflectionTestUtils.setField(shareMock, "id", 1L);
		ReflectionTestUtils.setField(shareMock, "createdAt", LocalDateTime.now());
		ReflectionTestUtils.setField(shareMock, "updatedAt", LocalDateTime.now());

		ShareResponse mockResponse = ShareResponse.from(shareMock);
		given(shareService.createShare(eq(surveyId), eq(creatorId), eq(shareMethod), eq(recipientIds))).willReturn(mockResponse);

		//when, then
		mockMvc.perform(post(URI)
			.contentType(MediaType.APPLICATION_JSON)
			.content(requestJson))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.success").value(true))
			.andExpect(jsonPath("$.data.surveyId").value(1))
			.andExpect(jsonPath("$.data.creatorId").value(1))
			.andExpect(jsonPath("$.data.shareMethod").value("URL"))
			.andExpect(jsonPath("$.data.shareLink").value("https://example.com/share/12345"))
			.andExpect(jsonPath("$.data.createdAt").exists())
			.andExpect(jsonPath("$.data.updatedAt").exists());
	}

	@Test
	@DisplayName("공유 생성 api - email 정상 요청, 201 return")
	void createShare_success_email() throws Exception {
		//given
		Long surveyId = 1L;
		Long creatorId = 1L;
		ShareMethod shareMethod = ShareMethod.EMAIL;
		String shareLink = "email://12345";
		List<Long> recipientIds = List.of(2L, 3L, 4L);

		String requestJson = """
			{
				\"surveyId\": 1,
				\"creatorId\": 1,
				\"shareMethod\": \"EMAIL\"
			}
			""";
		Share shareMock = new Share(surveyId, creatorId, shareMethod, shareLink, recipientIds);

		ReflectionTestUtils.setField(shareMock, "id", 1L);
		ReflectionTestUtils.setField(shareMock, "createdAt", LocalDateTime.now());
		ReflectionTestUtils.setField(shareMock, "updatedAt", LocalDateTime.now());

		ShareResponse mockResponse = ShareResponse.from(shareMock);
		given(shareService.createShare(eq(surveyId), eq(creatorId), eq(shareMethod), eq(recipientIds))).willReturn(mockResponse);

		//when, then
		mockMvc.perform(post(URI)
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestJson))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.success").value(true))
			.andExpect(jsonPath("$.data.surveyId").value(1))
			.andExpect(jsonPath("$.data.creatorId").value(1))
			.andExpect(jsonPath("$.data.shareMethod").value("EMAIL"))
			.andExpect(jsonPath("$.data.shareLink").value("email://12345"))
			.andExpect(jsonPath("$.data.createdAt").exists())
			.andExpect(jsonPath("$.data.updatedAt").exists());
	}

	@Test
	@DisplayName("공유 생성 api - 요청 body 누락, 400 return")
	void createShare_fail_noSurveyId() throws Exception {
		//given
		String requestJson = "{}";

		//when, then
		mockMvc.perform(post(URI)
			.contentType(MediaType.APPLICATION_JSON)
			.content(requestJson))
			.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("알림 이력 조회 성공 - 정상 요청")
	void getAllNotifications_success() throws Exception {
		//given
		Long shareId = 1L;
		Long currentUserId = 1L;
		int page = 0;
		int size = 10;

		NotificationResponse mockNotification = new NotificationResponse(
			1L, currentUserId, Status.SENT, LocalDateTime.now(), null
		);
		PageInfo pageInfo = new PageInfo(size, page, 1, 1);
		NotificationPageResponse response = new NotificationPageResponse(List.of(mockNotification), pageInfo);

		given(notificationService.gets(eq(shareId), eq(currentUserId), eq(page), eq(size))).willReturn(response);

		//when, then
		mockMvc.perform(get("/api/v1/share-tasks/{shareId}/notifications", shareId)
				.param("page", String.valueOf(page))
				.param("size", String.valueOf(size)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message").value("알림 이력 조회 성공"))
			.andExpect(jsonPath("$.data.content[0].id").value(1))
			.andExpect(jsonPath("$.data.content[0].recipientId").value(1))
			.andExpect(jsonPath("$.data.content[0].status").value("SENT"))
			.andExpect(jsonPath("$.data.pageInfo.totalElements").value(1))
			.andDo(print());
	}

	@Test
	@DisplayName("알림 이력 조회 실패 - 존재하지 않는 공유 ID")
	void getAllNotifications_invalidShareId() throws Exception {
		//given
		Long invalidShareId = 999L;
		Long currentUserId = 1L;
		int page = 0;
		int size = 0;

		NotificationResponse mockNotification = new NotificationResponse(
			1L, currentUserId, Status.SENT, LocalDateTime.now(), null
		);
		PageInfo pageInfo = new PageInfo(size, page, 1, 1);
		NotificationPageResponse response = new NotificationPageResponse(List.of(mockNotification), pageInfo);

		given(notificationService.gets(eq(invalidShareId), eq(currentUserId), eq(page), eq(size)))
			.willThrow(new CustomException(CustomErrorCode.NOT_FOUND_SHARE));

		//when, then
		mockMvc.perform(get("/api/v1/share-tasks/{shareId}/notifications", invalidShareId)
				.param("page", String.valueOf(page))
				.param("size", String.valueOf(size)))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.success").value(false))
			.andExpect(jsonPath("$.message").value("공유 작업이 존재하지 않습니다."))
			.andDo(print());
	}
}
