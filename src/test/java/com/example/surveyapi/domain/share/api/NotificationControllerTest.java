package com.example.surveyapi.domain.share.api;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import com.example.surveyapi.domain.share.api.notification.NotificationController;
import com.example.surveyapi.domain.share.application.notification.NotificationService;
import com.example.surveyapi.domain.share.application.notification.dto.NotificationPageResponse;
import com.example.surveyapi.domain.share.application.notification.dto.NotificationResponse;
import com.example.surveyapi.domain.share.domain.notification.vo.Status;
import com.example.surveyapi.global.enums.CustomErrorCode;
import com.example.surveyapi.global.exception.CustomException;
import com.example.surveyapi.global.util.PageInfo;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@AutoConfigureMockMvc
@WebMvcTest(NotificationController.class)
@TestPropertySource(properties = "SECRET_KEY=123456789012345678901234567890")
class NotificationControllerTest {
	@Autowired
	private MockMvc mockMvc;
	@MockBean
	private NotificationService notificationService;

	@BeforeEach
	void setUp() {
		TestingAuthenticationToken auth =
			new TestingAuthenticationToken(1L, null, "ROLE_USER");
		auth.setAuthenticated(true);
		SecurityContextHolder.getContext().setAuthentication(auth);
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
