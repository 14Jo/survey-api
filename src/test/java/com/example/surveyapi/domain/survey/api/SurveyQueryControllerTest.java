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

import com.example.surveyapi.domain.survey.application.QueryService.SurveyReadService;
import com.example.surveyapi.domain.survey.application.response.SearchSurveyDetailResponse;
import com.example.surveyapi.domain.survey.application.response.SearchSurveyStatusResponse;
import com.example.surveyapi.domain.survey.application.response.SearchSurveyTitleResponse;
import com.example.surveyapi.domain.survey.domain.query.dto.SurveyDetail;
import com.example.surveyapi.domain.survey.domain.query.dto.SurveyStatusList;
import com.example.surveyapi.domain.survey.domain.query.dto.SurveyTitle;
import com.example.surveyapi.domain.survey.domain.survey.enums.SurveyStatus;
import com.example.surveyapi.domain.survey.domain.survey.vo.SurveyDuration;
import com.example.surveyapi.domain.survey.domain.survey.vo.SurveyOption;
import com.example.surveyapi.global.enums.CustomErrorCode;
import com.example.surveyapi.global.exception.CustomException;
import com.example.surveyapi.global.exception.GlobalExceptionHandler;

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

        SurveyDetail surveyDetail = SurveyDetail.of(1L, "title", "description", SurveyStatus.PREPARING,
            SurveyDuration.of(LocalDateTime.now(), LocalDateTime.now().plusDays(1)),
            SurveyOption.of(true, true), List.of());
        surveyDetailResponse = SearchSurveyDetailResponse.from(surveyDetail, 3);

        SurveyTitle surveyTitle = SurveyTitle.of(1L, "title", SurveyOption.of(true, true), SurveyStatus.PREPARING,
            SurveyDuration.of(LocalDateTime.now(), LocalDateTime.now().plusDays(1)));
        surveyTitleResponse = SearchSurveyTitleResponse.from(surveyTitle, 3);

        surveyStatusResponse = SearchSurveyStatusResponse.from(new SurveyStatusList(List.of(1L, 2L, 3L)));
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
                .param("surveyStatus", "ACTIVE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("조회 성공"))
                .andExpect(jsonPath("$.data").exists());
    }
} 