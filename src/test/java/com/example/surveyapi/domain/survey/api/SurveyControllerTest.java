package com.example.surveyapi.domain.survey.api;

import com.example.surveyapi.domain.survey.application.command.SurveyService;
import com.example.surveyapi.domain.survey.application.command.dto.request.CreateSurveyRequest;
import com.example.surveyapi.domain.survey.application.command.dto.request.UpdateSurveyRequest;
import com.example.surveyapi.domain.survey.application.command.dto.request.SurveyRequest;
import com.example.surveyapi.domain.survey.domain.question.enums.QuestionType;
import com.example.surveyapi.domain.survey.domain.survey.enums.SurveyType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SurveyController.class)
@ActiveProfiles("test")
@Import(SurveyControllerTest.TestSecurityConfig.class)
class SurveyControllerTest {

    @TestConfiguration
    @EnableWebSecurity
    static class TestSecurityConfig {
        
        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
            http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/api/v1/projects/**").permitAll()
                    .requestMatchers("/api/v1/surveys/**").permitAll()
                    .anyRequest().authenticated()
                );
            return http.build();
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SurveyService surveyService;

    @Autowired
    private ObjectMapper objectMapper;

    private CreateSurveyRequest validCreateRequest;

    private Authentication createMockAuthentication() {
        return new UsernamePasswordAuthenticationToken(
            1L, null, List.of(new SimpleGrantedAuthority("ROLE_USER")));
    }

    @BeforeEach
    void setUp() {
        objectMapper.registerModule(new JavaTimeModule());
        
        validCreateRequest = new CreateSurveyRequest();
        ReflectionTestUtils.setField(validCreateRequest, "title", "테스트 설문");
        ReflectionTestUtils.setField(validCreateRequest, "description", "테스트 설문 설명");
        ReflectionTestUtils.setField(validCreateRequest, "surveyType", SurveyType.SURVEY);
        
        SurveyRequest.Duration duration = new SurveyRequest.Duration();
        ReflectionTestUtils.setField(duration, "startDate", LocalDateTime.now().plusDays(1));
        ReflectionTestUtils.setField(duration, "endDate", LocalDateTime.now().plusDays(10));
        ReflectionTestUtils.setField(validCreateRequest, "surveyDuration", duration);
        
        SurveyRequest.Option option = new SurveyRequest.Option();
        ReflectionTestUtils.setField(option, "anonymous", true);
        ReflectionTestUtils.setField(option, "allowResponseUpdate", false);
        ReflectionTestUtils.setField(validCreateRequest, "surveyOption", option);
        
        SurveyRequest.QuestionRequest question = new SurveyRequest.QuestionRequest();
        ReflectionTestUtils.setField(question, "content", "테스트 질문");
        ReflectionTestUtils.setField(question, "questionType", QuestionType.SHORT_ANSWER);
        ReflectionTestUtils.setField(question, "isRequired", true);
        ReflectionTestUtils.setField(question, "displayOrder", 1);
        ReflectionTestUtils.setField(question, "choices", List.of());
        ReflectionTestUtils.setField(validCreateRequest, "questions", List.of(question));
    }

    @Test
    @DisplayName("설문 생성 - 유효한 요청")
    void createSurvey_validRequest_success() throws Exception {
        // given
        when(surveyService.create(anyString(), anyLong(), anyLong(), any(CreateSurveyRequest.class)))
            .thenReturn(1L);

        Authentication auth = new UsernamePasswordAuthenticationToken(
            1L, null, List.of(new SimpleGrantedAuthority("ROLE_USER")));

        // when & then
        mockMvc.perform(post("/api/v1/projects/1/surveys")
                .header("Authorization", "Bearer valid-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validCreateRequest))
                .with(authentication(auth)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(1L));
    }

    @Test
    @DisplayName("설문 생성 - 제목이 null인 경우 실패")
    void createSurvey_nullTitle_badRequest() throws Exception {
        // given
        ReflectionTestUtils.setField(validCreateRequest, "title", null);

        // when & then
        mockMvc.perform(post("/api/v1/projects/1/surveys")
                .header("Authorization", "Bearer valid-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validCreateRequest))
                .with(authentication(createMockAuthentication())))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("설문 생성 - 제목이 빈 문자열인 경우 실패")
    void createSurvey_emptyTitle_badRequest() throws Exception {
        // given
        ReflectionTestUtils.setField(validCreateRequest, "title", "");

        // when & then
        mockMvc.perform(post("/api/v1/projects/1/surveys")
                .header("Authorization", "Bearer valid-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validCreateRequest))
                .with(authentication(createMockAuthentication())))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("설문 생성 - 설문 타입이 null인 경우 실패")
    void createSurvey_nullSurveyType_badRequest() throws Exception {
        // given
        ReflectionTestUtils.setField(validCreateRequest, "surveyType", null);

        // when & then
        mockMvc.perform(post("/api/v1/projects/1/surveys")
                .header("Authorization", "Bearer valid-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validCreateRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("설문 생성 - Content-Type이 JSON이 아닌 경우 실패")
    void createSurvey_invalidContentType_unsupportedMediaType() throws Exception {
        // when & then
        mockMvc.perform(post("/api/v1/projects/1/surveys")
                .header("Authorization", "Bearer valid-token")
                .contentType(MediaType.TEXT_PLAIN)
                .content("invalid content"))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    @DisplayName("설문 생성 - 잘못된 JSON 형식인 경우 실패")
    void createSurvey_invalidJson_badRequest() throws Exception {
        // when & then
        mockMvc.perform(post("/api/v1/projects/1/surveys")
                .header("Authorization", "Bearer valid-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ invalid json }"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Authorization 헤더 누락 시 실패")
    void request_withoutAuthorizationHeader_badRequest() throws Exception {
        // when & then
        mockMvc.perform(post("/api/v1/projects/1/surveys")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validCreateRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("잘못된 PathVariable 타입 - 문자열을 Long으로 변환 실패")
    void request_invalidPathVariable_badRequest() throws Exception {
        // when & then
        mockMvc.perform(post("/api/v1/projects/invalid/surveys")
                .header("Authorization", "Bearer valid-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validCreateRequest))
                .with(authentication(createMockAuthentication())))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("설문 수정 - 제목만 수정하는 유효한 요청")
    void updateSurvey_titleOnly_success() throws Exception {
        // given
        UpdateSurveyRequest titleOnlyRequest = new UpdateSurveyRequest();
        ReflectionTestUtils.setField(titleOnlyRequest, "title", "제목만 수정");
        
        // validation을 위한 필수 필드들 설정
        SurveyRequest.Duration duration = new SurveyRequest.Duration();
        ReflectionTestUtils.setField(duration, "startDate", LocalDateTime.now().plusDays(1));
        ReflectionTestUtils.setField(duration, "endDate", LocalDateTime.now().plusDays(10));
        ReflectionTestUtils.setField(titleOnlyRequest, "surveyDuration", duration);
        
        when(surveyService.update(anyString(), anyLong(), anyLong(), any(UpdateSurveyRequest.class)))
            .thenReturn(1L);

        // when & then
        mockMvc.perform(put("/api/v1/surveys/1")
                .header("Authorization", "Bearer valid-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(titleOnlyRequest))
                .with(authentication(createMockAuthentication())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(1L));
    }
}