package com.example.surveyapi.domain.participation.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.example.surveyapi.domain.participation.application.dto.request.CreateParticipationRequest;
import com.example.surveyapi.domain.participation.application.dto.request.ResponseData;
import com.fasterxml.jackson.databind.ObjectMapper;

@TestPropertySource(properties = "SECRET_KEY=SecretKeyExample42534D@DAF!1243zvjnjw@")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ParticipationControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@AfterEach
	void tearDown() {
		SecurityContextHolder.clearContext();
	}

	@Test
	@DisplayName("설문 응답 제출 api")
	void createParticipation() throws Exception {
		// given
		Long surveyId = 1L;
		Long memberId = 1L;

		SecurityContextHolder.getContext().setAuthentication(
			new UsernamePasswordAuthenticationToken(
				memberId, null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
			)
		);

		ResponseData responseData1 = new ResponseData(1L, Map.of("textAnswer", "주관식 및 서술형"));
		ResponseData responseData2 = new ResponseData(2L, Map.of("choices", List.of(1, 3)));
		List<ResponseData> responseDataList = List.of(responseData1, responseData2);

		CreateParticipationRequest request = new CreateParticipationRequest(responseDataList);

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
	void createParticipation_fail() throws Exception {
		// given
		Long surveyId = 1L;
		Long memberId = 1L;

		SecurityContextHolder.getContext().setAuthentication(
			new UsernamePasswordAuthenticationToken(
				memberId, null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
			)
		);

		CreateParticipationRequest request = new CreateParticipationRequest(Collections.emptyList());

		// when & then
		mockMvc.perform(post("/api/v1/surveys/{surveyId}/participations", surveyId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isBadRequest()) // HTTP 400 Bad Request
			.andExpect(jsonPath("$.message").value("유효성 검증 실패"))
			.andExpect(jsonPath("$.data.responseDataList").value("응답 데이터는 최소 1개 이상이어야 합니다."));
	}
}

