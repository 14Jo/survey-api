package com.example.surveyapi.domain.survey.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.example.surveyapi.domain.survey.application.qeury.SurveyReadService;
import com.example.surveyapi.domain.survey.application.dto.response.SearchSurveyDetailResponse;
import com.example.surveyapi.domain.survey.application.dto.response.SearchSurveyStatusResponse;
import com.example.surveyapi.domain.survey.application.dto.response.SearchSurveyTitleResponse;
import com.example.surveyapi.domain.survey.domain.survey.enums.SurveyStatus;
import com.example.surveyapi.domain.survey.domain.survey.enums.ScheduleState;
import com.example.surveyapi.global.exception.CustomErrorCode;
import com.example.surveyapi.global.exception.CustomException;
import com.example.surveyapi.global.exception.GlobalExceptionHandler;
import com.example.surveyapi.domain.survey.domain.query.SurveyReadEntity;

@ExtendWith(MockitoExtension.class)
class SurveyQueryControllerTest {

    @Mock
    private SurveyReadService surveyReadService;

    @InjectMocks
    private SurveyQueryController surveyQueryController;

    private MockMvc mockMvc;
    private SearchSurveyDetailResponse surveyDetailResponse;
    private SearchSurveyTitleResponse surveyTitleResponse;
    private SearchSurveyStatusResponse surveyStatusResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(surveyQueryController)
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();

        surveyDetailResponse = createSurveyDetailResponse();

        surveyTitleResponse = createSurveyTitleResponse();

        surveyStatusResponse = SearchSurveyStatusResponse.from(List.of(1L, 2L, 3L));
    }

    @Test
    @DisplayName("설문 상세 조회 - 성공")
    void getSurveyDetail_success() throws Exception {
        // given
        when(surveyReadService.findSurveyDetailById(anyLong())).thenReturn(surveyDetailResponse);

        // when & then
        mockMvc.perform(get("/api/v1/surveys/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("조회 성공"))
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    @DisplayName("설문 상세 조회 - 설문 없음 실패")
    void getSurveyDetail_fail_not_found() throws Exception {
        // given
        when(surveyReadService.findSurveyDetailById(anyLong()))
                .thenThrow(new CustomException(CustomErrorCode.NOT_FOUND_SURVEY));

        // when & then
        mockMvc.perform(get("/api/v1/surveys/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("설문이 존재하지 않습니다"));
    }

    @Test
    @DisplayName("프로젝트 설문 목록 조회 - 성공")
    void getSurveyList_success() throws Exception {
        // given
        when(surveyReadService.findSurveyByProjectId(anyLong(), any()))
                .thenReturn(List.of(surveyTitleResponse));

        // when & then
        mockMvc.perform(get("/api/v1/projects/1/surveys"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("조회 성공"))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @DisplayName("프로젝트 설문 목록 조회 - 커서 기반 페이징 성공")
    void getSurveyList_with_cursor_success() throws Exception {
        // given
        when(surveyReadService.findSurveyByProjectId(anyLong(), any()))
                .thenReturn(List.of(surveyTitleResponse));

        // when & then
        mockMvc.perform(get("/api/v1/projects/1/surveys")
                .param("lastSurveyId", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("조회 성공"))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @DisplayName("설문 목록 조회 (v2) - 성공")
    void getSurveyList_v2_success() throws Exception {
        // given
        when(surveyReadService.findSurveys(any())).thenReturn(List.of(surveyTitleResponse));

        // when & then
        mockMvc.perform(get("/api/v2/survey/find-surveys")
                .param("surveyIds", "1", "2", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("조회 성공"))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @DisplayName("설문 상태 조회 - 성공")
    void getSurveyStatus_success() throws Exception {
        // given
        when(surveyReadService.findBySurveyStatus(anyString())).thenReturn(surveyStatusResponse);

        // when & then
        mockMvc.perform(get("/api/v2/survey/find-status")
                .param("surveyStatus", "PREPARING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("조회 성공"))
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    @DisplayName("설문 상태 조회 - 잘못된 상태값 실패")
    void getSurveyStatus_fail_invalid_status() throws Exception {
        // given
        when(surveyReadService.findBySurveyStatus(anyString()))
                .thenThrow(new CustomException(CustomErrorCode.STATUS_INVALID_FORMAT));

        // when & then
        mockMvc.perform(get("/api/v2/survey/find-status")
                .param("surveyStatus", "INVALID_STATUS"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    private SearchSurveyDetailResponse createSurveyDetailResponse() {
        SurveyReadEntity.SurveyOptions options = new SurveyReadEntity.SurveyOptions(
            true, true, LocalDateTime.now(), LocalDateTime.now().plusDays(7)
        );
        
        SurveyReadEntity entity = SurveyReadEntity.create(
            1L, 1L, "테스트 설문", "테스트 설문 설명", 
            SurveyStatus.PREPARING, ScheduleState.AUTO_SCHEDULED, 5, options
        );
        
        return SearchSurveyDetailResponse.from(entity, 5);
    }

    private SearchSurveyTitleResponse createSurveyTitleResponse() {
        SurveyReadEntity.SurveyOptions options = new SurveyReadEntity.SurveyOptions(
            true, true, LocalDateTime.now(), LocalDateTime.now().plusDays(7)
        );
        
        SurveyReadEntity entity = SurveyReadEntity.create(
            1L, 1L, "테스트 설문", "테스트 설문 설명", 
            SurveyStatus.PREPARING, ScheduleState.AUTO_SCHEDULED, 5, options
        );
        
        return SearchSurveyTitleResponse.from(entity);
    }
} 