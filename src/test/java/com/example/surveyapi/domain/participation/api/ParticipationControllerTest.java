package com.example.surveyapi.domain.participation.api;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import com.example.surveyapi.domain.participation.application.ParticipationService;
import com.example.surveyapi.domain.participation.application.client.SurveyInfoDto;
import com.example.surveyapi.domain.participation.application.dto.request.CreateParticipationRequest;
import com.example.surveyapi.domain.participation.application.dto.response.ParticipationDetailResponse;
import com.example.surveyapi.domain.participation.application.dto.response.ParticipationInfoResponse;
import com.example.surveyapi.domain.participation.domain.command.ResponseData;
import com.example.surveyapi.domain.participation.domain.participation.Participation;
import com.example.surveyapi.domain.participation.domain.participation.enums.Gender;
import com.example.surveyapi.domain.participation.domain.participation.query.ParticipationInfo;
import com.example.surveyapi.domain.participation.domain.participation.vo.ParticipantInfo;
import com.example.surveyapi.domain.survey.domain.survey.enums.SurveyStatus;
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
		mockMvc.perform(post("/api/v1/surveys/{surveyId}/participations", surveyId)
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
		mockMvc.perform(post("/api/v1/surveys/{surveyId}/participations", surveyId)
				.header("Authorization", "Bearer test-token")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value("요청 데이터 검증에 실패하였습니다."))
			.andExpect(jsonPath("$.data.responseDataList").value("응답 데이터는 최소 1개 이상이어야 합니다."));
	}

	@DisplayName("나의 전체 참여 목록 조회 API")
	@Test
	void getAllMyParticipation() throws Exception {
		// given
		authenticateUser(1L);
		Pageable pageable = PageRequest.of(0, 5);

		ParticipationInfo p1 = new ParticipationInfo(1L, 1L, LocalDateTime.now().minusWeeks(1));
		SurveyInfoDto dto1 = createSurveyInfoDto(1L, "설문 제목1");
		ParticipationInfoResponse.SurveyInfoOfParticipation s1 = ParticipationInfoResponse.SurveyInfoOfParticipation.from(
			dto1);

		ParticipationInfo p2 = new ParticipationInfo(2L, 2L, LocalDateTime.now().minusWeeks(1));
		SurveyInfoDto dto2 = createSurveyInfoDto(2L, "설문 제목2");
		ParticipationInfoResponse.SurveyInfoOfParticipation s2 = ParticipationInfoResponse.SurveyInfoOfParticipation.from(
			dto2);

		List<ParticipationInfoResponse> participationResponses = List.of(
			ParticipationInfoResponse.of(p1, s1),
			ParticipationInfoResponse.of(p2, s2)
		);
		Page<ParticipationInfoResponse> pageResponse = new PageImpl<>(participationResponses, pageable,
			participationResponses.size());
		when(participationService.gets(anyString(), eq(1L), any(Pageable.class))).thenReturn(pageResponse);

		// when & then
		mockMvc.perform(get("/api/v1/members/me/participations")
				.header("Authorization", "Bearer test-token")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message").value("나의 참여 목록 조회에 성공하였습니다."))
			.andExpect(jsonPath("$.data.content[0].surveyInfo.title").value("설문 제목1"))
			.andExpect(jsonPath("$.data.content[1].surveyInfo.title").value("설문 제목2"));
	}

	private SurveyInfoDto createSurveyInfoDto(Long id, String title) {
		SurveyInfoDto dto = new SurveyInfoDto();
		ReflectionTestUtils.setField(dto, "surveyId", id);
		ReflectionTestUtils.setField(dto, "title", title);
		ReflectionTestUtils.setField(dto, "status", SurveyStatus.IN_PROGRESS);
		SurveyInfoDto.Duration duration = new SurveyInfoDto.Duration();
		ReflectionTestUtils.setField(duration, "endDate", LocalDateTime.now().plusDays(1));
		ReflectionTestUtils.setField(dto, "duration", duration);
		SurveyInfoDto.Option option = new SurveyInfoDto.Option();
		ReflectionTestUtils.setField(option, "allowResponseUpdate", true);
		ReflectionTestUtils.setField(dto, "option", option);
		return dto;
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
		mockMvc.perform(post("/api/v1/surveys/{surveyId}/participations", surveyId)
				.header("Authorization", "Bearer test-token")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isConflict())
			.andExpect(jsonPath("$.message").value(CustomErrorCode.SURVEY_ALREADY_PARTICIPATED.getMessage()));
	}

	@Test
	@DisplayName("나의 참여 응답 상세 조회 API")
	void getParticipation() throws Exception {
		// given
		Long participationId = 1L;
		Long userId = 1L;
		authenticateUser(userId);

		List<ResponseData> responseDataList = List.of(createResponseData(1L, Map.of("text", "응답 상세 조회")));

		ParticipationDetailResponse serviceResult = ParticipationDetailResponse.from(
			Participation.create(userId, 1L, ParticipantInfo.of("2000-01-01T00:00:00", Gender.MALE, "서울", "강남구"),
				responseDataList)
		);
		ReflectionTestUtils.setField(serviceResult, "participationId", participationId);

		when(participationService.get(eq(userId), eq(participationId))).thenReturn(serviceResult);

		// when & then
		mockMvc.perform(get("/api/v1/participations/{participationId}", participationId)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message").value("참여 응답 상세 조회에 성공하였습니다."))
			.andExpect(jsonPath("$.data.participationId").value(participationId))
			.andExpect(jsonPath("$.data.responses[0].answer.text").value("응답 상세 조회"));
	}

	@Test
	@DisplayName("나의 참여 응답 상세 조회 API 실패 - 참여 기록 없음")
	void getParticipation_notFound() throws Exception {
		// given
		Long participationId = 999L;
		Long userId = 1L;
		authenticateUser(userId);

		doThrow(new CustomException(CustomErrorCode.NOT_FOUND_PARTICIPATION))
			.when(participationService).get(eq(userId), eq(participationId));

		// when & then
		mockMvc.perform(get("/api/v1/participations/{participationId}", participationId)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.message").value(CustomErrorCode.NOT_FOUND_PARTICIPATION.getMessage()));
	}

	@Test
	@DisplayName("나의 참여 응답 상세 조회 API 실패 - 접근 권한 없음")
	void getParticipation_accessDenied() throws Exception {
		// given
		Long participationId = 1L;
		Long userId = 1L;
		authenticateUser(userId);

		doThrow(new CustomException(CustomErrorCode.ACCESS_DENIED_PARTICIPATION_VIEW))
			.when(participationService).get(eq(userId), eq(participationId));

		// when & then
		mockMvc.perform(get("/api/v1/participations/{participationId}", participationId)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isForbidden())
			.andExpect(jsonPath("$.message").value(CustomErrorCode.ACCESS_DENIED_PARTICIPATION_VIEW.getMessage()));
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
		mockMvc.perform(put("/api/v1/participations/{participationId}", participationId)
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
		mockMvc.perform(put("/api/v1/participations/{participationId}", participationId)
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
		mockMvc.perform(put("/api/v1/participations/{participationId}", participationId)
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
		mockMvc.perform(put("/api/v1/participations/{participationId}", participationId)
				.header("Authorization", "Bearer test-token")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value("요청 데이터 검증에 실패하였습니다."))
			.andExpect(jsonPath("$.data.responseDataList").value("응답 데이터는 최소 1개 이상이어야 합니다."));
	}
}