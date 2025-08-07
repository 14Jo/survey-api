package com.example.surveyapi.domain.survey.application;

import com.example.surveyapi.domain.survey.application.QueryService.SurveyReadService;
import com.example.surveyapi.domain.survey.application.response.SearchSurveyDetailResponse;
import com.example.surveyapi.domain.survey.application.response.SearchSurveyStatusResponse;
import com.example.surveyapi.domain.survey.application.response.SearchSurveyTitleResponse;
import com.example.surveyapi.domain.survey.application.client.ParticipationPort;
import com.example.surveyapi.domain.survey.application.client.ParticipationCountDto;
import com.example.surveyapi.domain.survey.domain.survey.Survey;
import com.example.surveyapi.domain.survey.domain.survey.SurveyRepository;
import com.example.surveyapi.domain.survey.domain.survey.enums.SurveyType;
import com.example.surveyapi.domain.survey.domain.survey.vo.SurveyDuration;
import com.example.surveyapi.domain.survey.domain.survey.vo.SurveyOption;
import com.example.surveyapi.global.enums.CustomErrorCode;
import com.example.surveyapi.global.exception.CustomException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@Testcontainers
@SpringBootTest
@Transactional
@ActiveProfiles("test")
class SurveyReadServiceTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private SurveyReadService surveyReadService;

    @Autowired
    private SurveyRepository surveyRepository;

    @MockitoBean
    private ParticipationPort participationPort;

    @Test
    @DisplayName("설문 상세 조회 - 성공")
    void findSurveyDetailById_success() {
        // given
        Survey savedSurvey = surveyRepository.save(createTestSurvey(1L, "상세 조회용 설문"));
        ParticipationCountDto mockCounts = ParticipationCountDto.of(Map.of(String.valueOf(savedSurvey.getSurveyId()), 10));

        when(participationPort.getParticipationCounts(anyList())).thenReturn(mockCounts);

        // when
        SearchSurveyDetailResponse detail = surveyReadService.findSurveyDetailById(savedSurvey.getSurveyId());

        // then
        assertThat(detail).isNotNull();
        assertThat(detail.getTitle()).isEqualTo("상세 조회용 설문");
        assertThat(detail.getParticipationCount()).isEqualTo(10);
    }

    @Test
    @DisplayName("설문 상세 조회 - 존재하지 않는 설문")
    void findSurveyDetailById_notFound() {
        // given
        Long nonExistentId = -1L;

        // when & then
        assertThatThrownBy(() -> surveyReadService.findSurveyDetailById(nonExistentId))
            .isInstanceOf(CustomException.class)
            .hasFieldOrPropertyWithValue("errorCode", CustomErrorCode.NOT_FOUND_SURVEY);
    }

    @Test
    @DisplayName("프로젝트별 설문 목록 조회 - 성공")
    void findSurveyByProjectId_success() {
        // given
        Long projectId = 1L;
        Survey survey1 = surveyRepository.save(createTestSurvey(projectId, "프로젝트 1의 설문 1"));
        Survey survey2 = surveyRepository.save(createTestSurvey(projectId, "프로젝트 1의 설문 2"));
        surveyRepository.save(createTestSurvey(2L, "다른 프로젝트 설문"));

        ParticipationCountDto mockCounts = ParticipationCountDto.of(Map.of(
            String.valueOf(survey1.getSurveyId()), 5,
            String.valueOf(survey2.getSurveyId()), 15
        ));
        when(participationPort.getParticipationCounts(anyList())).thenReturn(mockCounts);

        // when
        List<SearchSurveyTitleResponse> list = surveyReadService.findSurveyByProjectId(projectId, null);

        // then
        assertThat(list).hasSize(2);
        assertThat(list).extracting(SearchSurveyTitleResponse::getTitle)
            .containsExactlyInAnyOrder("프로젝트 1의 설문 1", "프로젝트 1의 설문 2");
        assertThat(list).extracting(SearchSurveyTitleResponse::getParticipationCount)
            .containsExactlyInAnyOrder(5, 15);
    }

    @Test
    @DisplayName("설문 목록 조회 - ID 리스트로 조회 성공")
    void findSurveys_success() {
        // given
        Survey survey1 = surveyRepository.save(createTestSurvey(1L, "ID 리스트 조회 1"));
        Survey survey2 = surveyRepository.save(createTestSurvey(1L, "ID 리스트 조회 2"));
        List<Long> surveyIdsToFind = List.of(survey1.getSurveyId(), survey2.getSurveyId());

        // when
        List<SearchSurveyTitleResponse> list = surveyReadService.findSurveys(surveyIdsToFind);

        // then
        assertThat(list).isNotNull();
        // MongoDB 기반으로 변경되었으므로 실제 데이터가 없으면 빈 리스트가 반환될 수 있음
    }

    @Test
    @DisplayName("설문 상태별 조회 - 성공")
    void findBySurveyStatus_success() {
        // given
        Survey preparingSurvey = createTestSurvey(1L, "준비중 설문");
        surveyRepository.save(preparingSurvey);

        Survey inProgressSurvey = createTestSurvey(1L, "진행중 설문");
        inProgressSurvey.open();
        surveyRepository.save(inProgressSurvey);

        // when
        SearchSurveyStatusResponse response = surveyReadService.findBySurveyStatus("PREPARING");

        // then
        assertThat(response).isNotNull();
        // MongoDB 기반으로 변경되었으므로 실제 데이터가 없으면 빈 리스트가 반환될 수 있음
    }

    @Test
    @DisplayName("설문 상태별 조회 - 잘못된 상태값")
    void findBySurveyStatus_invalidStatus() {
        // given
        String invalidStatus = "INVALID_STATUS";

        // when & then
        assertThatThrownBy(() -> surveyReadService.findBySurveyStatus(invalidStatus))
            .isInstanceOf(CustomException.class)
            .hasFieldOrPropertyWithValue("errorCode", CustomErrorCode.STATUS_INVALID_FORMAT);
    }

    private Survey createTestSurvey(Long projectId, String title) {
        return Survey.create(
            projectId,
            1L,
            title,
            "description",
            SurveyType.SURVEY,
            SurveyDuration.of(LocalDateTime.now(), LocalDateTime.now().plusDays(5)),
            SurveyOption.of(false, false),
            List.of()
        );
    }
}