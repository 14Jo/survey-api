package com.example.surveyapi.domain.participation.api;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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

import com.example.surveyapi.domain.participation.application.ParticipationQueryService;
import com.example.surveyapi.domain.participation.application.client.SurveyInfoDto;
import com.example.surveyapi.domain.participation.application.client.enums.SurveyApiStatus;
import com.example.surveyapi.domain.participation.application.dto.response.ParticipationDetailResponse;
import com.example.surveyapi.domain.participation.application.dto.response.ParticipationGroupResponse;
import com.example.surveyapi.domain.participation.application.dto.response.ParticipationInfoResponse;
import com.example.surveyapi.domain.participation.domain.command.ResponseData;
import com.example.surveyapi.domain.participation.domain.participation.query.ParticipationInfo;
import com.example.surveyapi.domain.participation.domain.participation.query.ParticipationProjection;
import com.example.surveyapi.global.exception.CustomErrorCode;
import com.example.surveyapi.global.exception.CustomException;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(ParticipationQueryController.class)
@AutoConfigureMockMvc(addFilters = false)
class ParticipationQueryControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private ParticipationQueryService participationQueryService;

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

	private SurveyInfoDto createSurveyInfoDto(Long id, String title) {
		SurveyInfoDto dto = new SurveyInfoDto();
		ReflectionTestUtils.setField(dto, "surveyId", id);
		ReflectionTestUtils.setField(dto, "title", title);
		ReflectionTestUtils.setField(dto, "status", SurveyApiStatus.IN_PROGRESS);
		SurveyInfoDto.Duration duration = new SurveyInfoDto.Duration();
		ReflectionTestUtils.setField(duration, "endDate", LocalDateTime.now().plusDays(1));
		ReflectionTestUtils.setField(dto, "duration", duration);
		SurveyInfoDto.Option option = new SurveyInfoDto.Option();
		ReflectionTestUtils.setField(option, "allowResponseUpdate", true);
		ReflectionTestUtils.setField(dto, "option", option);
		return dto;
	}

	@Test
	@DisplayName("여러 설문에 대한 모든 참여 응답 기록 조회 API")
	void getAllBySurveyIds() throws Exception {
		// given
		List<Long> surveyIds = List.of(10L, 20L);

		ParticipationProjection projection1 = new ParticipationProjection(10L, 1L, LocalDateTime.now(),
			Collections.emptyList());
		ParticipationProjection projection2 = new ParticipationProjection(10L, 2L, LocalDateTime.now(),
			Collections.emptyList());

		ParticipationDetailResponse detail1 = ParticipationDetailResponse.fromProjection(projection1);
		ParticipationDetailResponse detail2 = ParticipationDetailResponse.fromProjection(projection2);

		ParticipationGroupResponse group1 = ParticipationGroupResponse.of(10L, List.of(detail1, detail2));
		ParticipationGroupResponse group2 = ParticipationGroupResponse.of(20L, Collections.emptyList());

		List<ParticipationGroupResponse> serviceResult = List.of(group1, group2);

		when(participationQueryService.getAllBySurveyIds(eq(surveyIds))).thenReturn(serviceResult);

		// when & then
		mockMvc.perform(get("/api/surveys/participations")
				.param("surveyIds", "10", "20"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message").value("여러 참여 기록 조회에 성공하였습니다."))
			.andExpect(jsonPath("$.data.length()").value(2))
			.andExpect(jsonPath("$.data[0].surveyId").value(10L))
			.andExpect(jsonPath("$.data[0].participations[0].participationId").value(1L))
			.andExpect(jsonPath("$.data[1].surveyId").value(20L))
			.andExpect(jsonPath("$.data[1].participations").isEmpty());
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
		when(participationQueryService.gets(anyString(), eq(1L), any(Pageable.class))).thenReturn(pageResponse);

		// when & then
		mockMvc.perform(get("/api/members/me/participations")
				.header("Authorization", "Bearer test-token")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message").value("나의 참여 목록 조회에 성공하였습니다."))
			.andExpect(jsonPath("$.data.content[0].surveyInfo.title").value("설문 제목1"))
			.andExpect(jsonPath("$.data.content[1].surveyInfo.title").value("설문 제목2"));
	}

	@Test
	@DisplayName("나의 참여 응답 상세 조회 API")
	void getParticipation() throws Exception {
		// given
		Long participationId = 1L;
		Long userId = 1L;
		authenticateUser(userId);

		ParticipationProjection projection = new ParticipationProjection(1L, participationId, LocalDateTime.now(),
			List.of(createResponseData(1L, Map.of("text", "응답 상세 조회"))));

		ParticipationDetailResponse serviceResult = ParticipationDetailResponse.fromProjection(projection);

		when(participationQueryService.get(eq(userId), eq(participationId))).thenReturn(serviceResult);

		// when & then
		mockMvc.perform(get("/api/participations/{participationId}", participationId)
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
			.when(participationQueryService).get(eq(userId), eq(participationId));

		// when & then
		mockMvc.perform(get("/api/participations/{participationId}", participationId)
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
			.when(participationQueryService).get(eq(userId), eq(participationId));

		// when & then
		mockMvc.perform(get("/api/participations/{participationId}", participationId)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isForbidden())
			.andExpect(jsonPath("$.message").value(CustomErrorCode.ACCESS_DENIED_PARTICIPATION_VIEW.getMessage()));
	}

	@Test
	@DisplayName("여러 설문의 참여 수 조회 API")
	void getParticipationCounts() throws Exception {
		// given
		List<Long> surveyIds = List.of(1L, 2L, 3L);
		Map<Long, Long> counts = Map.of(1L, 10L, 2L, 30L, 3L, 0L);

		when(participationQueryService.getCountsBySurveyIds(surveyIds)).thenReturn(counts);

		// when & then
		mockMvc.perform(get("/api/surveys/participations/count")
				.param("surveyIds", "1", "2", "3"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message").value("참여 count 성공"))
			.andExpect(jsonPath("$.data.1").value(10L))
			.andExpect(jsonPath("$.data.2").value(30L))
			.andExpect(jsonPath("$.data.3").value(0L));
	}
}