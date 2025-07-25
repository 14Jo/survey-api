package com.example.surveyapi.domain.participation.api;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.example.surveyapi.domain.participation.application.ParticipationService;
import com.example.surveyapi.domain.participation.application.dto.request.SurveyInfoOfParticipation;
import com.example.surveyapi.domain.participation.application.dto.response.ReadParticipationPageResponse;
import com.example.surveyapi.domain.participation.domain.participation.Participation;
import com.example.surveyapi.domain.participation.domain.participation.vo.ParticipantInfo;

@WebMvcTest(ParticipationController.class)
class ParticipationControllerUnitTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private ParticipationService participationService;

	@Test
	@WithMockUser
	@DisplayName("나의 전체 참여 응답 조회 API")
	void getAllParticipations() throws Exception {
		// given
		Long memberId = 1L;
		Pageable pageable = PageRequest.of(0, 5);

		Participation p1 = Participation.create(memberId, 1L, new ParticipantInfo());
		SurveyInfoOfParticipation s1 = SurveyInfoOfParticipation.of(1L, "설문 제목1", "진행 중",
			LocalDate.now().plusWeeks(1), true);
		Participation p2 = Participation.create(memberId, 2L, new ParticipantInfo());
		SurveyInfoOfParticipation s2 = SurveyInfoOfParticipation.of(2L, "설문 제목2", "종료", LocalDate.now().minusWeeks(1),
			false);

		List<ReadParticipationPageResponse> participationResponses = List.of(
			ReadParticipationPageResponse.of(p1, s1),
			ReadParticipationPageResponse.of(p2, s2)
		);
		Page<ReadParticipationPageResponse> pageResponse = new PageImpl<>(participationResponses, pageable,
			participationResponses.size());

		when(participationService.gets(anyLong(), any(Pageable.class))).thenReturn(pageResponse);

		// when & then
		mockMvc.perform(get("/api/v1/members/me/participations")
				.contentType(MediaType.APPLICATION_JSON)
				.principal(() -> "1"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message").value("나의 전체 설문 참여 기록 조회에 성공하였습니다."))
			.andExpect(jsonPath("$.data.content[0].surveyInfo.surveyTitle").value("설문 제목1"))
			.andExpect(jsonPath("$.data.content[1].surveyInfo.surveyTitle").value("설문 제목2"));
	}
}
