package com.example.surveyapi.domain.survey.api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = "SECRET_KEY=12345678901234567890123456789012")
@WithMockUser(username = "testuser", roles = "USER")
class SurveyControllerTest {

	@Autowired
	MockMvc mockMvc;

	@Test
	@DisplayName("설문 생성 API - 정상 케이스")
	void createSurvey_success() throws Exception {
		// given
		String requestJson = """
			{
			    \"title\": \"설문 제목\",
			    \"description\": \"설문 설명\",
			    \"surveyType\": \"VOTE\",
			    \"surveyDuration\": { \"startDate\": \"2025-09-01T00:00:00\", \"endDate\": \"2025-09-10T23:59:59\" },
			    \"surveyOption\": { \"anonymous\": true, \"allowMultipleResponses\": false, \"allowResponseUpdate\": true },
			    \"questions\": [
			        { \"content\": \"Q1\", \"questionType\": \"SHORT_ANSWER\", \"displayOrder\": 1, \"choices\": [], \"required\": true }
			    ]
			}
			""";

		// when & then
		mockMvc.perform(post("/api/v1/survey/1/create")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestJson))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.success").value(true));
	}

	@Test
	@DisplayName("설문 생성 API - 필수값 누락시 404")
	void createSurvey_fail_validation() throws Exception {
		// given
		String requestJson = "{}";

		// when & then
		mockMvc.perform(post("/api/v1/survey/1/create")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestJson))
			.andExpect(status().isBadRequest());
	}
} 