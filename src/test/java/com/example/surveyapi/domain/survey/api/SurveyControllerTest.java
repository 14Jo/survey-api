package com.example.surveyapi.domain.survey.api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.surveyapi.domain.survey.application.SurveyService;

@WebMvcTest(SurveyController.class)
@AutoConfigureMockMvc(addFilters = false)
class SurveyControllerTest {

	@Autowired
	MockMvc mockMvc;

	@MockBean
	SurveyService surveyService;

	@MockBean
	com.example.surveyapi.domain.survey.application.SurveyQueryService surveyQueryService;

	private final String createUri = "/api/v1/survey/1/create";

	@Test
	@DisplayName("설문 생성 API - 필수값 누락시 400")
	void createSurvey_fail_requiredField() throws Exception {
		// given
		String requestJson = """
			{
				\"description\": \"설문 설명\",
				\"surveyType\": \"VOTE\",
				\"surveyDuration\": { \"startDate\": \"2025-09-01T00:00:00\", \"endDate\": \"2025-09-10T23:59:59\" },
				\"surveyOption\": { \"anonymous\": true, \"allowMultipleResponses\": false, \"allowResponseUpdate\": true },
				\"questions\": []
			}
			""";
		// when & then
		mockMvc.perform(post(createUri)
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestJson))
			.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("설문 생성 API - 잘못된 Enum 값 입력시 400")
	void createSurvey_fail_invalidEnum() throws Exception {
		// given
		String requestJson = """
			{
				\"title\": \"설문 제목\",
				\"description\": \"설문 설명\",
				\"surveyType\": \"FailTest\",
				\"surveyDuration\": { \"startDate\": \"2025-09-01T00:00:00\", \"endDate\": \"2025-09-10T23:59:59\" },
				\"surveyOption\": { \"anonymous\": true, \"allowMultipleResponses\": false, \"allowResponseUpdate\": true },
				\"questions\": []
			}
			""";
		// when & then
		mockMvc.perform(post(createUri)
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestJson))
			.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("설문 생성 API - 설문 기간 유효성(종료일이 시작일보다 빠름) 400")
	void createSurvey_fail_invalidDuration() throws Exception {
		// given
		String requestJson = """
			{
				\"title\": \"설문 제목\",
				\"description\": \"설문 설명\",
				\"surveyType\": \"VOTE\",
				\"surveyDuration\": { \"startDate\": \"2025-09-10T23:59:59\", \"endDate\": \"2025-09-01T00:00:00\" },
				\"surveyOption\": { \"anonymous\": true, \"allowMultipleResponses\": false, \"allowResponseUpdate\": true },
				\"questions\": []
			}
			""";
		// when & then
		mockMvc.perform(post(createUri)
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestJson))
			.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("설문 생성 API - 질문 필수값 누락시 400")
	void createSurvey_questionRequiredField() throws Exception {
		// given
		String requestJson = """
			{
			    \"surveyType\": \"VOTE\",
			    \"surveyDuration\": { \"startDate\": \"2025-09-01T00:00:00\", \"endDate\": \"2025-09-10T23:59:59\" },
			    \"surveyOption\": { \"anonymous\": true, \"allowMultipleResponses\": false, \"allowResponseUpdate\": true },
			    \"questions\": [
			        { \"content\": \"\", \"questionType\": \"SHORT_ANSWER\", \"displayOrder\": 1, \"choices\": [], \"required\": true }
			    ]
			}
			""";
		// when & then
		mockMvc.perform(post(createUri)
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestJson))
			.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("설문 생성 API - 질문 타입별 유효성(선택지 필수) 400")
	void createSurvey_questionTypeValidation() throws Exception {
		// given
		String requestJson = """
			{
			    \"title\": \"설문 제목\",
			    \"surveyType\": \"VOTE\",
			    \"surveyDuration\": { \"startDate\": \"2025-09-01T00:00:00\", \"endDate\": \"2025-09-10T23:59:59\" },
			    \"surveyOption\": { \"anonymous\": true, \"allowMultipleResponses\": false, \"allowResponseUpdate\": true },
			    \"questions\": [
			        { \"content\": \"Q1\", \"questionType\": \"MULTIPLE_CHOICE\", \"displayOrder\": 1, \"choices\": [], \"required\": true }
			    ]
			}
			""";
		// when & then
		mockMvc.perform(post(createUri)
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestJson))
			.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("설문 생성 API - 정상 입력시 201")
	void createSurvey_success_mock() throws Exception {
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
		given(surveyService.create(any(Long.class), any(Long.class), any())).willReturn(123L);

		// when & then
		mockMvc.perform(post(createUri)
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestJson))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.success").value(true));
	}

	@Test
	@DisplayName("설문 수정 API - 값 전부 누락 시 400")
	void updateSurvey_requestValidation() throws Exception {
		// given
		String requestJson = """
			{}
			""";

		// when & then
		mockMvc.perform(put("/api/v1/survey/1/update")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestJson))
			.andExpect(status().isBadRequest());
	}
} 