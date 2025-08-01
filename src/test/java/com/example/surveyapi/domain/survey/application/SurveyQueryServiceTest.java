package com.example.surveyapi.domain.survey.application;

import com.example.surveyapi.domain.survey.application.client.ParticipationPort;
import com.example.surveyapi.domain.survey.application.client.ParticipationCountDto;
import com.example.surveyapi.domain.survey.application.response.SearchSurveyDetailResponse;
import com.example.surveyapi.domain.survey.application.response.SearchSurveyStatusResponse;
import com.example.surveyapi.domain.survey.application.response.SearchSurveyTitleResponse;
import com.example.surveyapi.domain.survey.domain.query.QueryRepository;
import com.example.surveyapi.domain.survey.domain.query.dto.SurveyDetail;
import com.example.surveyapi.domain.survey.domain.query.dto.SurveyStatusList;
import com.example.surveyapi.domain.survey.domain.query.dto.SurveyTitle;
import com.example.surveyapi.domain.survey.domain.survey.Survey;
import com.example.surveyapi.domain.survey.domain.survey.enums.SurveyStatus;
import com.example.surveyapi.domain.survey.domain.survey.enums.SurveyType;
import com.example.surveyapi.domain.survey.domain.survey.vo.SurveyDuration;
import com.example.surveyapi.domain.survey.domain.survey.vo.SurveyOption;
import com.example.surveyapi.global.enums.CustomErrorCode;
import com.example.surveyapi.global.exception.CustomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SurveyQueryServiceTest {

    @Mock
    private QueryRepository surveyQueryRepository;

    @Mock
    private ParticipationPort participationPort;

    @InjectMocks
    private SurveyQueryService surveyQueryService;

    private SurveyDetail mockSurveyDetail;
    private SurveyTitle mockSurveyTitle;
    private ParticipationCountDto mockParticipationCounts;
    private String authHeader;

    @BeforeEach
    void setUp() {
        // given
        authHeader = "Bearer test-token";
        
        mockSurveyDetail = SurveyDetail.of(
            Survey.create(1L, 1L, "title", "desc", SurveyType.VOTE,
                SurveyDuration.of(LocalDateTime.now(), LocalDateTime.now().plusDays(1)),
                SurveyOption.of(true, true), List.of()),
            List.of()
        );

        mockSurveyTitle = SurveyTitle.of(1L, "title", SurveyOption.of(true, true), SurveyStatus.PREPARING,
            SurveyDuration.of(LocalDateTime.now(), LocalDateTime.now().plusDays(1)));

        Map<String, Integer> participationCounts = Map.of("1", 5, "2", 3);
        mockParticipationCounts = ParticipationCountDto.of(participationCounts);
    }

    @Test
    @DisplayName("설문 상세 조회 - 성공")
    void findSurveyDetailById_success() {
        // given
        when(surveyQueryRepository.getSurveyDetail(1L)).thenReturn(Optional.of(mockSurveyDetail));
        when(participationPort.getParticipationCounts(anyString(), anyList()))
            .thenReturn(mockParticipationCounts);

        // when
        SearchSurveyDetailResponse detail = surveyQueryService.findSurveyDetailById(authHeader, 1L);

        // then
        assertThat(detail).isNotNull();
        assertThat(detail.getTitle()).isEqualTo("title");
        assertThat(detail.getParticipationCount()).isEqualTo(5);
        verify(participationPort).getParticipationCounts(authHeader, List.of(1L));
    }

    @Test
    @DisplayName("설문 상세 조회 - 존재하지 않는 설문")
    void findSurveyDetailById_notFound() {
        // given
        when(surveyQueryRepository.getSurveyDetail(-1L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> surveyQueryService.findSurveyDetailById(authHeader, -1L))
            .isInstanceOf(CustomException.class)
            .hasFieldOrPropertyWithValue("errorCode", CustomErrorCode.NOT_FOUND_SURVEY);
    }

    @Test
    @DisplayName("설문 상세 조회 - 참여 수가 null인 경우")
    void findSurveyDetailById_nullParticipationCount() {
        // given
        when(surveyQueryRepository.getSurveyDetail(1L)).thenReturn(Optional.of(mockSurveyDetail));
        Map<String, Integer> emptyCounts = Map.of();
        ParticipationCountDto emptyParticipationCounts = ParticipationCountDto.of(emptyCounts);
        when(participationPort.getParticipationCounts(anyString(), anyList()))
            .thenReturn(emptyParticipationCounts);

        // when
        SearchSurveyDetailResponse detail = surveyQueryService.findSurveyDetailById(authHeader, 1L);

        // then
        assertThat(detail).isNotNull();
        assertThat(detail.getParticipationCount()).isNull();
    }

    @Test
    @DisplayName("프로젝트별 설문 목록 조회 - 성공")
    void findSurveyByProjectId_success() {
        // given
        List<SurveyTitle> surveyTitles = List.of(mockSurveyTitle);
        when(surveyQueryRepository.getSurveyTitles(1L, null)).thenReturn(surveyTitles);
        when(participationPort.getParticipationCounts(anyString(), anyList()))
            .thenReturn(mockParticipationCounts);

        // when
        List<SearchSurveyTitleResponse> list = surveyQueryService.findSurveyByProjectId(authHeader, 1L, null);

        // then
        assertThat(list).isNotNull();
        assertThat(list).hasSize(1);
        assertThat(list.get(0).getTitle()).isEqualTo("title");
        assertThat(list.get(0).getParticipationCount()).isEqualTo(5);
        verify(participationPort).getParticipationCounts(authHeader, List.of(1L));
    }

    @Test
    @DisplayName("프로젝트별 설문 목록 조회 - 빈 목록")
    void findSurveyByProjectId_emptyList() {
        // given
        when(surveyQueryRepository.getSurveyTitles(1L, null)).thenReturn(List.of());
        when(participationPort.getParticipationCounts(anyString(), anyList()))
            .thenReturn(ParticipationCountDto.of(Map.of()));

        // when
        List<SearchSurveyTitleResponse> list = surveyQueryService.findSurveyByProjectId(authHeader, 1L, null);

        // then
        assertThat(list).isNotNull();
        assertThat(list).isEmpty();
    }

    @Test
    @DisplayName("프로젝트별 설문 목록 조회 - 커서 기반 페이징")
    void findSurveyByProjectId_withCursor() {
        // given
        List<SurveyTitle> surveyTitles = List.of(mockSurveyTitle);
        when(surveyQueryRepository.getSurveyTitles(1L, 10L)).thenReturn(surveyTitles);
        when(participationPort.getParticipationCounts(anyString(), anyList()))
            .thenReturn(mockParticipationCounts);

        // when
        List<SearchSurveyTitleResponse> list = surveyQueryService.findSurveyByProjectId(authHeader, 1L, 10L);

        // then
        assertThat(list).isNotNull();
        assertThat(list).hasSize(1);
        verify(surveyQueryRepository).getSurveyTitles(1L, 10L);
    }

    @Test
    @DisplayName("설문 목록 조회 - ID 리스트로 조회 성공")
    void findSurveys_success() {
        // given
        List<SurveyTitle> surveyTitles = List.of(mockSurveyTitle);
        when(surveyQueryRepository.getSurveys(List.of(1L, 2L))).thenReturn(surveyTitles);

        // when
        List<SearchSurveyTitleResponse> list = surveyQueryService.findSurveys(List.of(1L, 2L));

        // then
        assertThat(list).isNotNull();
        assertThat(list).hasSize(1);
        assertThat(list.get(0).getParticipationCount()).isNull();
    }

    @Test
    @DisplayName("설문 목록 조회 - 빈 ID 리스트")
    void findSurveys_emptyList() {
        // given
        when(surveyQueryRepository.getSurveys(List.of())).thenReturn(List.of());

        // when
        List<SearchSurveyTitleResponse> list = surveyQueryService.findSurveys(List.of());

        // then
        assertThat(list).isNotNull();
        assertThat(list).isEmpty();
    }

    @Test
    @DisplayName("설문 상태별 조회 - 성공")
    void findBySurveyStatus_success() {
        // given
        SurveyStatusList mockStatusList = new SurveyStatusList(List.of(1L, 2L, 3L));
        when(surveyQueryRepository.getSurveyStatusList(SurveyStatus.PREPARING)).thenReturn(mockStatusList);

        // when
        SearchSurveyStatusResponse response = surveyQueryService.findBySurveyStatus("PREPARING");

        // then
        assertThat(response).isNotNull();
        assertThat(response.getSurveyIds()).containsExactly(1L, 2L, 3L);
    }

    @Test
    @DisplayName("설문 상태별 조회 - 잘못된 상태값")
    void findBySurveyStatus_invalidStatus() {
        // given
        
        // when & then
        assertThatThrownBy(() -> surveyQueryService.findBySurveyStatus("INVALID_STATUS"))
            .isInstanceOf(CustomException.class)
            .hasFieldOrPropertyWithValue("errorCode", CustomErrorCode.STATUS_INVALID_FORMAT);
    }

    @Test
    @DisplayName("설문 상태별 조회 - 대소문자 구분 없는 상태값")
    void findBySurveyStatus_caseInsensitive() {
        // given
        SurveyStatusList mockStatusList = new SurveyStatusList(List.of(1L, 2L, 3L));
        when(surveyQueryRepository.getSurveyStatusList(SurveyStatus.IN_PROGRESS)).thenReturn(mockStatusList);

        // when
        SearchSurveyStatusResponse response = surveyQueryService.findBySurveyStatus("IN_PROGRESS");

        // then
        assertThat(response).isNotNull();
        assertThat(response.getSurveyIds()).containsExactly(1L, 2L, 3L);
    }
} 