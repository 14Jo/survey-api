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
import org.springframework.test.web.servlet.MockMvc;

import com.example.surveyapi.domain.participation.application.ParticipationService;
import com.example.surveyapi.domain.participation.application.dto.response.ParticipationDetailResponse;
import com.example.surveyapi.domain.participation.application.dto.response.ParticipationGroupResponse;
import com.example.surveyapi.domain.participation.domain.participation.query.ParticipationProjection;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(ParticipationInternalController.class)
@AutoConfigureMockMvc(addFilters = false)
class ParticipationInternalControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private ParticipationService participationService;

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

		when(participationService.getAllBySurveyIds(eq(surveyIds))).thenReturn(serviceResult);

		// when & then
		mockMvc.perform(get("/api/v1/surveys/participations")
				.param("surveyIds", "10", "20"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message").value("여러 참여 기록 조회에 성공하였습니다."))
			.andExpect(jsonPath("$.data.length()").value(2))
			.andExpect(jsonPath("$.data[0].surveyId").value(10L))
			.andExpect(jsonPath("$.data[0].participations[0].participationId").value(1L))
			.andExpect(jsonPath("$.data[1].surveyId").value(20L))
			.andExpect(jsonPath("$.data[1].participations").isEmpty());
	}

	@Test
	@DisplayName("여러 설문의 참여 수 조회 API")
	void getParticipationCounts() throws Exception {
		// given
		List<Long> surveyIds = List.of(1L, 2L, 3L);
		Map<Long, Long> counts = Map.of(1L, 10L, 2L, 30L, 3L, 0L);

		when(participationService.getCountsBySurveyIds(surveyIds)).thenReturn(counts);

		// when & then
		mockMvc.perform(get("/api/v2/surveys/participations/count")
				.param("surveyIds", "1", "2", "3"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message").value("참여 count 성공"))
			.andExpect(jsonPath("$.data.1").value(10L))
			.andExpect(jsonPath("$.data.2").value(30L))
			.andExpect(jsonPath("$.data.3").value(0L));
	}
}