package com.example.surveyapi.participation.api;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import com.example.surveyapi.participation.application.ParticipationQueryService;
import com.example.surveyapi.participation.application.ParticipationService;
import com.example.surveyapi.participation.application.dto.request.CreateParticipationRequest;
import com.example.surveyapi.participation.domain.command.ResponseData;
import com.example.surveyapi.global.exception.CustomErrorCode;
import com.example.surveyapi.global.exception.CustomException;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(ParticipationController.class)
@AutoConfigureMockMvc(addFilters = false)
class ParticipationControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private ParticipationService participationService;

	@MockBean
	private ParticipationQueryService participationQueryService;

	@AfterEach
	void tearDown() {
		SecurityContextHolder.clearContext();
	}

	private void authenticateUser(Long userId) {
		SecurityContextHolder.getContext().setAuthentication(
			new UsernamePasswordAuthenticationToken(
				userId, null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
			)
		);
	}

	private ResponseData createResponseData(Long questionId, Map<String, Object> answer) {
		ResponseData responseData = new ResponseData();
		ReflectionTestUtils.setField(responseData, "questionId", questionId);
		ReflectionTestUtils.setField(responseData, "answer", answer);

		return responseData;
	}

	@Test
	@DisplayName("설문 응답 제출 api")
	void createParticipation() throws Exception {
		// given
		Long surveyId = 1L;
		authenticateUser(1L);

		ResponseData responseData = createResponseData(1L, Map.of("textAnswer", "주관식 및 서술형"));

		List<ResponseData> responseDataList = new ArrayList<>(List.of(responseData));

		CreateParticipationRequest request = new CreateParticipationRequest();
		ReflectionTestUtils.setField(request, "responseDataList", responseDataList);

		// when & then
		mockMvc.perform(post("/api/surveys/{surveyId}/participations", surveyId)
				.header("Authorization", "Bearer test-token")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.message").value("설문 응답 제출이 완료되었습니다."))
			.andExpect(jsonPath("$.data").isNumber());
	}

	@Test
	@DisplayName("설문 응답 제출 실패 - 비어있는 responseData")
	void createParticipation_emptyResponseData() throws Exception {
		// given
		Long surveyId = 1L;
		authenticateUser(1L);

		CreateParticipationRequest request = new CreateParticipationRequest();
		ReflectionTestUtils.setField(request, "responseDataList", Collections.emptyList());

		// when & then
		mockMvc.perform(post("/api/surveys/{surveyId}/participations", surveyId)
				.header("Authorization", "Bearer test-token")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value("요청 데이터 검증에 실패하였습니다."))
			.andExpect(jsonPath("$.data.responseDataList").value("응답 데이터는 최소 1개 이상이어야 합니다."));
	}

	@Test
	@DisplayName("설문 응답 제출 실패 - 중복 예외 발생")
	void createParticipation_conflictException() throws Exception {
		// given
		Long surveyId = 1L;
		authenticateUser(1L);

		ResponseData responseData = createResponseData(1L, Map.of("textAnswer", "답변"));

		CreateParticipationRequest request = new CreateParticipationRequest();
		ReflectionTestUtils.setField(request, "responseDataList", List.of(responseData));

		doThrow(new CustomException(CustomErrorCode.SURVEY_ALREADY_PARTICIPATED))
			.when(participationService)
			.create(anyString(), eq(surveyId), eq(1L), any(CreateParticipationRequest.class));

		// when & then
		mockMvc.perform(post("/api/surveys/{surveyId}/participations", surveyId)
				.header("Authorization", "Bearer test-token")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isConflict())
			.andExpect(jsonPath("$.message").value(CustomErrorCode.SURVEY_ALREADY_PARTICIPATED.getMessage()));
	}

	@Test
	@DisplayName("잘못된 질문 ID로 요청 시 400 에러")
	void createParticipation_invalidQuestion() throws Exception {
		// given
		Long surveyId = 1L;
		authenticateUser(1L);

		CreateParticipationRequest request = new CreateParticipationRequest();
		ReflectionTestUtils.setField(request, "responseDataList", List.of(createResponseData(1L, Map.of("text", ""))));

		doThrow(new CustomException(CustomErrorCode.INVALID_SURVEY_QUESTION))
			.when(participationService).create(anyString(), anyLong(), anyLong(), any());

		// when & then
		mockMvc.perform(post("/api/surveys/{surveyId}/participations", surveyId)
				.header("Authorization", "Bearer test-token")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value(CustomErrorCode.INVALID_SURVEY_QUESTION.getMessage()));
	}

	@Test
	@DisplayName("필수 질문 누락 시 400 에러")
	void createParticipation_missingRequiredQuestion() throws Exception {
		// given
		Long surveyId = 1L;
		authenticateUser(1L);

		CreateParticipationRequest request = new CreateParticipationRequest();
		ReflectionTestUtils.setField(request, "responseDataList", List.of(createResponseData(1L, Map.of("text", ""))));

		doThrow(new CustomException(CustomErrorCode.REQUIRED_QUESTION_NOT_ANSWERED))
			.when(participationService).create(anyString(), anyLong(), anyLong(), any());

		// when & then
		mockMvc.perform(post("/api/surveys/{surveyId}/participations", surveyId)
				.header("Authorization", "Bearer test-token")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value(CustomErrorCode.REQUIRED_QUESTION_NOT_ANSWERED.getMessage()));
	}

	@Test
	@DisplayName("참여 응답 수정 API")
	void updateParticipation() throws Exception {
		// given
		Long participationId = 1L;
		Long userId = 1L;
		authenticateUser(userId);

		CreateParticipationRequest request = new CreateParticipationRequest();
		ReflectionTestUtils.setField(request, "responseDataList",
			List.of(createResponseData(1L, Map.of("textAnswer", "수정된 답변"))));

		doNothing().when(participationService)
			.update(anyString(), eq(userId), eq(participationId), any(CreateParticipationRequest.class));

		// when & then
		mockMvc.perform(put("/api/participations/{participationId}", participationId)
				.header("Authorization", "Bearer test-token")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message").value("참여 응답 수정이 완료되었습니다."));
	}

	@Test
	@DisplayName("참여 응답 수정 API 실패 - 참여 기록 없음")
	void updateParticipation_notFound() throws Exception {
		// given
		Long participationId = 999L;
		Long userId = 1L;
		authenticateUser(userId);

		CreateParticipationRequest request = new CreateParticipationRequest();
		ReflectionTestUtils.setField(request, "responseDataList",
			List.of(createResponseData(1L, Map.of("textAnswer", "수정된 답변"))));

		doThrow(new CustomException(CustomErrorCode.NOT_FOUND_PARTICIPATION))
			.when(participationService)
			.update(anyString(), eq(userId), eq(participationId), any(CreateParticipationRequest.class));

		// when & then
		mockMvc.perform(put("/api/participations/{participationId}", participationId)
				.header("Authorization", "Bearer test-token")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.message").value(CustomErrorCode.NOT_FOUND_PARTICIPATION.getMessage()));
	}

	@Test
	@DisplayName("참여 응답 수정 API 실패 - 접근 권한 없음")
	void updateParticipation_accessDenied() throws Exception {
		// given
		Long participationId = 1L;
		Long userId = 1L;
		authenticateUser(userId);

		CreateParticipationRequest request = new CreateParticipationRequest();
		ReflectionTestUtils.setField(request, "responseDataList",
			List.of(createResponseData(1L, Map.of("textAnswer", "수정된 답변"))));

		doThrow(new CustomException(CustomErrorCode.ACCESS_DENIED_PARTICIPATION_VIEW))
			.when(participationService)
			.update(anyString(), eq(userId), eq(participationId), any(CreateParticipationRequest.class));

		// when & then
		mockMvc.perform(put("/api/participations/{participationId}", participationId)
				.header("Authorization", "Bearer test-token")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isForbidden())
			.andExpect(jsonPath("$.message").value(CustomErrorCode.ACCESS_DENIED_PARTICIPATION_VIEW.getMessage()));
	}

	@Test
	@DisplayName("참여 응답 수정 API 실패 - 비어있는 responseData")
	void updateParticipation_emptyResponseData() throws Exception {
		// given
		Long participationId = 1L;
		authenticateUser(1L);

		CreateParticipationRequest request = new CreateParticipationRequest();
		ReflectionTestUtils.setField(request, "responseDataList", Collections.emptyList());

		// when & then
		mockMvc.perform(put("/api/participations/{participationId}", participationId)
				.header("Authorization", "Bearer test-token")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value("요청 데이터 검증에 실패하였습니다."))
			.andExpect(jsonPath("$.data.responseDataList").value("응답 데이터는 최소 1개 이상이어야 합니다."));
	}
}