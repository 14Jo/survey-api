package com.example.surveyapi.domain.participation.api;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
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
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.example.surveyapi.domain.participation.application.ParticipationService;
import com.example.surveyapi.domain.participation.application.dto.request.CreateParticipationRequest;
import com.example.surveyapi.domain.participation.application.dto.request.ParticipationGroupRequest;
import com.example.surveyapi.domain.participation.application.dto.response.ParticipationDetailResponse;
import com.example.surveyapi.domain.participation.application.dto.response.ParticipationGroupResponse;
import com.example.surveyapi.domain.participation.application.dto.response.ParticipationInfoResponse;
import com.example.surveyapi.domain.participation.domain.command.ResponseData;
import com.example.surveyapi.domain.participation.domain.participation.Participation;
import com.example.surveyapi.domain.participation.domain.participation.query.ParticipationInfo;
import com.example.surveyapi.domain.participation.domain.participation.vo.ParticipantInfo;
import com.example.surveyapi.global.enums.CustomErrorCode;
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
	private com.example.surveyapi.domain.survey.application.SurveyQueryService surveyQueryService;

	@AfterEach
	void tearDown() {
		SecurityContextHolder.clearContext();
	}

	private void authenticateUser(Long memberId) {
		SecurityContextHolder.getContext().setAuthentication(
			new UsernamePasswordAuthenticationToken(
				memberId, null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
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
		ParticipationInfoResponse.SurveyInfoOfParticipation s1 = ParticipationInfoResponse.SurveyInfoOfParticipation.of(
			1L, "설문 제목1", "진행 중",
			LocalDate.now().plusWeeks(1), true);
		ParticipationInfo p2 = new ParticipationInfo(2L, 2L, LocalDateTime.now().minusWeeks(1));
		ParticipationInfoResponse.SurveyInfoOfParticipation s2 = ParticipationInfoResponse.SurveyInfoOfParticipation.of(
			2L, "설문 제목2", "종료", LocalDate.now().minusWeeks(1),
			false);

		List<ParticipationInfoResponse> participationResponses = List.of(
			ParticipationInfoResponse.of(p1, s1),
			ParticipationInfoResponse.of(p2, s2)
		);
		Page<ParticipationInfoResponse> pageResponse = new PageImpl<>(participationResponses, pageable,
			participationResponses.size());

		when(participationService.gets(eq(1L), any(Pageable.class))).thenReturn(pageResponse);

		// when & then
		mockMvc.perform(get("/api/v1/members/me/participations")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message").value("나의 참여 목록 조회에 성공하였습니다."))
			.andExpect(jsonPath("$.data.content[0].surveyInfo.surveyTitle").value("설문 제목1"))
			.andExpect(jsonPath("$.data.content[1].surveyInfo.surveyTitle").value("설문 제목2"));
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
			.when(participationService).create(eq(surveyId), eq(1L), any(CreateParticipationRequest.class));

		// when & then
		mockMvc.perform(post("/api/v1/surveys/{surveyId}/participations", surveyId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isConflict())
			.andExpect(jsonPath("$.message").value(CustomErrorCode.SURVEY_ALREADY_PARTICIPATED.getMessage()));
	}

	@Test
	@DisplayName("여러 설문에 대한 모든 참여 응답 기록 조회 API")
	void getAllBySurveyIds() throws Exception {
		// given
		Long memberId = 1L;
		authenticateUser(memberId);

		List<Long> surveyIds = List.of(10L, 20L);
		ParticipationGroupRequest request = new ParticipationGroupRequest();
		ReflectionTestUtils.setField(request, "surveyIds", surveyIds);

		ParticipationDetailResponse detail1 = ParticipationDetailResponse.from(
			Participation.create(memberId, 10L, new ParticipantInfo(),
				List.of(createResponseData(1L, Map.of("textAnswer", "answer1"))))
		);
		ReflectionTestUtils.setField(detail1, "participationId", 1L);

		ParticipationDetailResponse detail2 = ParticipationDetailResponse.from(
			Participation.create(memberId, 10L, new ParticipantInfo(),
				List.of(createResponseData(2L, Map.of("textAnswer", "answer2"))))
		);
		ReflectionTestUtils.setField(detail2, "participationId", 2L);

		ParticipationGroupResponse group1 = ParticipationGroupResponse.of(10L, List.of(detail1, detail2));
		ParticipationGroupResponse group2 = ParticipationGroupResponse.of(20L, Collections.emptyList());

		List<ParticipationGroupResponse> serviceResult = List.of(group1, group2);

		when(participationService.getAllBySurveyIds(eq(surveyIds))).thenReturn(serviceResult);

		// when & then
		mockMvc.perform(post("/api/v1/surveys/participations")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message").value("여러 참여 기록 조회에 성공하였습니다."))
			.andExpect(jsonPath("$.data.length()").value(2))
			.andExpect(jsonPath("$.data[0].surveyId").value(10L))
			.andExpect(jsonPath("$.data[0].participations[0].participationId").value(1L))
			.andExpect(jsonPath("$.data[0].participations[0].responses[0].answer.textAnswer").value("answer1"))
			.andExpect(jsonPath("$.data[1].surveyId").value(20L))
			.andExpect(jsonPath("$.data[1].participations").isEmpty());
	}

	@Test
	@DisplayName("여러 설문에 대한 모든 참여 응답 기록 조회 API 실패 - surveyIds 비어있음")
	void getAllBySurveyIds_emptyRequestSurveyIds() throws Exception {
		// given
		authenticateUser(1L);

		ParticipationGroupRequest request = new ParticipationGroupRequest();
		ReflectionTestUtils.setField(request, "surveyIds", Collections.emptyList());

		// when & then
		mockMvc.perform(post("/api/v1/surveys/participations")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value("요청 데이터 검증에 실패하였습니다."))
			.andExpect(jsonPath("$.data.surveyIds").value("must not be empty"));
	}

	@Test
	@DisplayName("나의 참여 응답 상세 조회 API")
	void getParticipation() throws Exception {
		// given
		Long participationId = 1L;
		Long memberId = 1L;
		authenticateUser(memberId);

		List<ResponseData> responseDataList = List.of(createResponseData(1L, Map.of("text", "응답 상세 조회")));

		ParticipationDetailResponse serviceResult = ParticipationDetailResponse.from(
			Participation.create(memberId, 1L, new ParticipantInfo(), responseDataList)
		);
		ReflectionTestUtils.setField(serviceResult, "participationId", participationId);

		when(participationService.get(eq(memberId), eq(participationId))).thenReturn(serviceResult);

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
		Long memberId = 1L;
		authenticateUser(memberId);

		doThrow(new CustomException(CustomErrorCode.NOT_FOUND_PARTICIPATION))
			.when(participationService).get(eq(memberId), eq(participationId));

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
		Long memberId = 1L;
		authenticateUser(memberId);

		doThrow(new CustomException(CustomErrorCode.ACCESS_DENIED_PARTICIPATION_VIEW))
			.when(participationService).get(eq(memberId), eq(participationId));

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
		Long memberId = 1L;
		authenticateUser(memberId);

		CreateParticipationRequest request = new CreateParticipationRequest();
		ReflectionTestUtils.setField(request, "responseDataList",
			List.of(createResponseData(1L, Map.of("textAnswer", "수정된 답변"))));

		doNothing().when(participationService)
			.update(eq(memberId), eq(participationId), any(CreateParticipationRequest.class));

		// when & then
		mockMvc.perform(put("/api/v1/participations/{participationId}", participationId)
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
		Long memberId = 1L;
		authenticateUser(memberId);

		CreateParticipationRequest request = new CreateParticipationRequest();
		ReflectionTestUtils.setField(request, "responseDataList",
			List.of(createResponseData(1L, Map.of("textAnswer", "수정된 답변"))));

		doThrow(new CustomException(CustomErrorCode.NOT_FOUND_PARTICIPATION))
			.when(participationService)
			.update(eq(memberId), eq(participationId), any(CreateParticipationRequest.class));

		// when & then
		mockMvc.perform(put("/api/v1/participations/{participationId}", participationId)
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
		Long memberId = 1L;
		authenticateUser(memberId);

		CreateParticipationRequest request = new CreateParticipationRequest();
		ReflectionTestUtils.setField(request, "responseDataList",
			List.of(createResponseData(1L, Map.of("textAnswer", "수정된 답변"))));

		doThrow(new CustomException(CustomErrorCode.ACCESS_DENIED_PARTICIPATION_VIEW))
			.when(participationService)
			.update(eq(memberId), eq(participationId), any(CreateParticipationRequest.class));

		// when & then
		mockMvc.perform(put("/api/v1/participations/{participationId}", participationId)
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
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value("요청 데이터 검증에 실패하였습니다."))
			.andExpect(jsonPath("$.data.responseDataList").value("응답 데이터는 최소 1개 이상이어야 합니다."));
	}
}
