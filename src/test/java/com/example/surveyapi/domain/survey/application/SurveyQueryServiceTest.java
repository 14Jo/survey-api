package com.example.surveyapi.domain.survey.application;

import com.example.surveyapi.domain.survey.application.response.SearchSurveyDtailResponse;
import com.example.surveyapi.domain.survey.application.response.SearchSurveyTitleResponse;
import com.example.surveyapi.domain.survey.domain.survey.SurveyRepository;
import com.example.surveyapi.domain.survey.domain.survey.enums.SurveyType;
import com.example.surveyapi.domain.survey.domain.survey.vo.SurveyDuration;
import com.example.surveyapi.domain.survey.domain.survey.vo.SurveyOption;
import com.example.surveyapi.domain.survey.application.request.CreateSurveyRequest;
import com.example.surveyapi.domain.survey.domain.question.enums.QuestionType;
import com.example.surveyapi.domain.survey.domain.survey.vo.QuestionInfo;
import com.example.surveyapi.global.exception.CustomException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class SurveyQueryServiceTest {

    @Autowired
    SurveyQueryService surveyQueryService;
    @Autowired
    SurveyService surveyService;

    @Test
    @DisplayName("상세 조회 - 정상 케이스")
    void findSurveyDetailById_success() {
        // given
        CreateSurveyRequest request = new CreateSurveyRequest();
        ReflectionTestUtils.setField(request, "title", "설문 제목");
        ReflectionTestUtils.setField(request, "surveyType", SurveyType.VOTE);
        ReflectionTestUtils.setField(request, "surveyDuration", new SurveyDuration(LocalDateTime.now(), LocalDateTime.now().plusDays(1)));
        ReflectionTestUtils.setField(request, "surveyOption", new SurveyOption(true, true));
        ReflectionTestUtils.setField(request, "questions", List.of(
                new QuestionInfo("Q1", QuestionType.SHORT_ANSWER, true, 1, List.of())
        ));
        Long surveyId = surveyService.create(1L, 1L, request);

        // when
        SearchSurveyDtailResponse detail = surveyQueryService.findSurveyDetailById(surveyId);

        // then
        assertThat(detail).isNotNull();
        assertThat(detail.getTitle()).isEqualTo("설문 제목");
    }

    @Test
    @DisplayName("상세 조회 - 없는 설문 예외")
    void findSurveyDetailById_notFound() {
        // when & then
        assertThatThrownBy(() -> surveyQueryService.findSurveyDetailById(-1L))
            .isInstanceOf(CustomException.class);
    }

    @Test
    @DisplayName("프로젝트별 설문 목록 조회 - 정상 케이스")
    void findSurveyByProjectId_success() {
        // given
        CreateSurveyRequest request = new CreateSurveyRequest();
        ReflectionTestUtils.setField(request, "title", "설문 제목");
        ReflectionTestUtils.setField(request, "surveyType", SurveyType.VOTE);
        ReflectionTestUtils.setField(request, "surveyDuration", new SurveyDuration(LocalDateTime.now(), LocalDateTime.now().plusDays(1)));
        ReflectionTestUtils.setField(request, "surveyOption", new SurveyOption(true, true));
        ReflectionTestUtils.setField(request, "questions", List.of());
        surveyService.create(1L, 1L, request);

        // when
        List<SearchSurveyTitleResponse> list = surveyQueryService.findSurveyByProjectId(1L, null);

        // then
        assertThat(list).isNotNull();
        assertThat(list.size()).isGreaterThanOrEqualTo(1);
    }
} 