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
		mockMvc.perform(post("/api/v1/survey/1/create")
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
				\"surveyType\": \"NOT_EXIST\",
				\"surveyDuration\": { \"startDate\": \"2025-09-01T00:00:00\", \"endDate\": \"2025-09-10T23:59:59\" },
				\"surveyOption\": { \"anonymous\": true, \"allowMultipleResponses\": false, \"allowResponseUpdate\": true },
				\"questions\": []
			}
			""";
		// when & then
		mockMvc.perform(post("/api/v1/survey/1/create")
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
		mockMvc.perform(post("/api/v1/survey/1/create")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestJson))
			.andExpect(status().isBadRequest());
	}

    @Test
    @DisplayName("설문 생성 API - 질문 displayOrder 중복/비연속 자동조정")
    void createSurvey_questionOrderAdjust() throws Exception {
        // given
        String requestJson = """
            {
                \"title\": \"설문 제목\",
                \"description\": \"설문 설명\",
                \"surveyType\": \"VOTE\",
                \"surveyDuration\": { \"startDate\": \"2025-09-01T00:00:00\", \"endDate\": \"2025-09-10T23:59:59\" },
                \"surveyOption\": { \"anonymous\": true, \"allowMultipleResponses\": false, \"allowResponseUpdate\": true },
                \"questions\": [
                    { \"content\": \"Q1\", \"questionType\": \"SHORT_ANSWER\", \"displayOrder\": 2, \"choices\": [], \"required\": true },
                    { \"content\": \"Q2\", \"questionType\": \"SHORT_ANSWER\", \"displayOrder\": 2, \"choices\": [], \"required\": true },
                    { \"content\": \"Q3\", \"questionType\": \"SHORT_ANSWER\", \"displayOrder\": 5, \"choices\": [], \"required\": true }
                ]
            }
            """;
        // when & then
        mockMvc.perform(post("/api/v1/survey/1/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.success").value(true));
        // 실제 DB 저장 및 displayOrder 조정은 통합 테스트/Query로 별도 검증 권장
    }

    @Test
    @DisplayName("설문 생성 API - 질문 필수값 누락시 400")
    void createSurvey_questionRequiredField() throws Exception {
        // given
        String requestJson = """
            {
                \"title\": \"설문 제목\",
                \"surveyType\": \"VOTE\",
                \"surveyDuration\": { \"startDate\": \"2025-09-01T00:00:00\", \"endDate\": \"2025-09-10T23:59:59\" },
                \"surveyOption\": { \"anonymous\": true, \"allowMultipleResponses\": false, \"allowResponseUpdate\": true },
                \"questions\": [
                    { \"content\": \"\", \"questionType\": \"SHORT_ANSWER\", \"displayOrder\": 1, \"choices\": [], \"required\": true }
                ]
            }
            """;
        // when & then
        mockMvc.perform(post("/api/v1/survey/1/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("설문 생성 API - 선택지 displayOrder 중복/비연속 자동조정")
    void createSurvey_choiceOrderAdjust() throws Exception {
        // given
        String requestJson = """
            {
                \"title\": \"설문 제목\",
                \"surveyType\": \"VOTE\",
                \"surveyDuration\": { \"startDate\": \"2025-09-01T00:00:00\", \"endDate\": \"2025-09-10T23:59:59\" },
                \"surveyOption\": { \"anonymous\": true, \"allowMultipleResponses\": false, \"allowResponseUpdate\": true },
                \"questions\": [
                    { \"content\": \"Q1\", \"questionType\": \"MULTIPLE_CHOICE\", \"displayOrder\": 1, \"choices\": [
                        { \"content\": \"A\", \"displayOrder\": 1 },
                        { \"content\": \"B\", \"displayOrder\": 1 },
                        { \"content\": \"C\", \"displayOrder\": 3 }
                    ], \"required\": true }
                ]
            }
            """;
        // when & then
        mockMvc.perform(post("/api/v1/survey/1/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.success").value(true));
        // 실제 DB 저장 및 displayOrder 조정은 통합 테스트/Query로 별도 검증 권장
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
        mockMvc.perform(post("/api/v1/survey/1/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
            .andExpect(status().isBadRequest());
    }
} 