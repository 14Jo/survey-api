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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import com.example.surveyapi.domain.share.application.notification.NotificationService;
import com.example.surveyapi.domain.share.application.notification.dto.NotificationResponse;
import com.example.surveyapi.domain.share.application.share.ShareService;
import com.example.surveyapi.domain.share.domain.notification.vo.Status;
import com.example.surveyapi.global.exception.CustomErrorCode;
import com.example.surveyapi.global.exception.CustomException;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(ShareController.class)
class ShareControllerTest {
	@Autowired
	private MockMvc mockMvc;
	@MockBean
	private ShareService shareService;
	@MockBean
	private NotificationService notificationService;

	private final String URI = "/api/v2/share-tasks";

	private final Long sourceId = 1L;
	private final Long creatorId = 1L;
	private final List<Long> recipientIds = List.of(2L, 3L);
	private final LocalDateTime notifyAt = LocalDateTime.now();
	private final LocalDateTime expirationDate = LocalDateTime.of(2025, 12, 31, 23, 59, 59);

	@BeforeEach
	void setUp() {
		TestingAuthenticationToken auth =
			new TestingAuthenticationToken(creatorId, null, "ROLE_USER");
		auth.setAuthenticated(true);
		SecurityContextHolder.getContext().setAuthentication(auth);
	}

	@Test
	@DisplayName("알림 이력 조회 성공 - 정상 요청")
	void getAllNotifications_success() throws Exception {
		//given
		Long shareId = 1L;
		int page = 0;
		int size = 10;

		NotificationResponse mockNotification = new NotificationResponse(
			1L, creatorId, Status.SENT, LocalDateTime.now(), null
		);
		List<NotificationResponse> content = List.of(mockNotification);
		Page<NotificationResponse> responses = new PageImpl<>(content, PageRequest.of(page, size), content.size());

		given(notificationService.gets(eq(shareId), eq(creatorId),
			eq(PageRequest.of(page, size)))).willReturn(responses);

		//when, then
		mockMvc.perform(get("/api/v1/share-tasks/{shareId}/notifications", shareId)
				.param("page", String.valueOf(page))
				.param("size", String.valueOf(size)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message").value("알림 이력 조회 성공"))
			.andExpect(jsonPath("$.data.content[0].id").value(1))
			.andExpect(jsonPath("$.data.content[0].recipientId").value(1))
			.andExpect(jsonPath("$.data.content[0].status").value("SENT"))
			.andExpect(jsonPath("$.data.totalElements").value(1))
			.andDo(print());
	}

	@Test
	@DisplayName("알림 이력 조회 실패 - 존재하지 않는 공유 ID")
	void getAllNotifications_invalidShareId() throws Exception {
		//given
		Long invalidShareId = 999L;
		int page = 0;
		int size = 10;

		given(notificationService.gets(eq(invalidShareId), eq(creatorId), eq(PageRequest.of(page, size))))
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
