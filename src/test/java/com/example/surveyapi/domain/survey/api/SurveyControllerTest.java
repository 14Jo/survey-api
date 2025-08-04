package com.example.surveyapi.domain.survey.api;

import com.example.surveyapi.domain.survey.application.SurveyService;
import com.example.surveyapi.domain.survey.application.request.CreateSurveyRequest;
import com.example.surveyapi.domain.survey.application.request.UpdateSurveyRequest;
import com.example.surveyapi.global.exception.GlobalExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class SurveyControllerTest {

    @Mock
    private SurveyService surveyService;

    @InjectMocks
    private SurveyController surveyController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(surveyController)
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    @DisplayName("설문 생성 요청 검증 - 잘못된 요청 실패")
    void createSurvey_request_validation_fail() throws Exception {
        // given
        CreateSurveyRequest invalidRequest = new CreateSurveyRequest();
        // 필수 필드가 없는 요청

        // when & then
        mockMvc.perform(post("/api/v1/projects/1/surveys")
                .header("Authorization", "Bearer token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("설문 수정 요청 검증 - 잘못된 요청 실패")
    void updateSurvey_request_validation_fail() throws Exception {
        // given
        UpdateSurveyRequest invalidRequest = new UpdateSurveyRequest();
        // 필수 필드가 없는 요청

        // when & then
        mockMvc.perform(put("/api/v1/surveys/1")
                .header("Authorization", "Bearer token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("설문 생성 요청 검증 - 잘못된 Content-Type 실패")
    void createSurvey_invalid_content_type_fail() throws Exception {
        // when & then
        mockMvc.perform(post("/api/v1/projects/1/surveys")
                .header("Authorization", "Bearer token")
                .contentType(MediaType.TEXT_PLAIN)
                .content("invalid content"))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    @DisplayName("설문 수정 요청 검증 - 잘못된 Content-Type 실패")
    void updateSurvey_invalid_content_type_fail() throws Exception {
        // when & then
        mockMvc.perform(put("/api/v1/surveys/1")
                .header("Authorization", "Bearer token")
                .contentType(MediaType.TEXT_PLAIN)
                .content("invalid content"))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    @DisplayName("설문 생성 요청 검증 - 잘못된 JSON 형식 실패")
    void createSurvey_invalid_json_fail() throws Exception {
        // when & then
        mockMvc.perform(post("/api/v1/projects/1/surveys")
                .header("Authorization", "Bearer token")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ invalid json }"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("설문 수정 요청 검증 - 잘못된 JSON 형식 실패")
    void updateSurvey_invalid_json_fail() throws Exception {
        // when & then
        mockMvc.perform(put("/api/v1/surveys/1")
                .header("Authorization", "Bearer token")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ invalid json }"))
                .andExpect(status().isBadRequest());
    }
} 