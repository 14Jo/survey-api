package com.example.surveyapi.domain.survey.application;

import com.example.surveyapi.domain.survey.application.request.CreateSurveyRequest;
import com.example.surveyapi.domain.survey.application.request.UpdateSurveyRequest;
import com.example.surveyapi.domain.survey.domain.survey.Survey;
import com.example.surveyapi.domain.survey.domain.survey.SurveyRepository;
import com.example.surveyapi.domain.survey.domain.survey.enums.SurveyType;
import com.example.surveyapi.domain.survey.domain.survey.vo.QuestionInfo;
import com.example.surveyapi.domain.survey.domain.survey.vo.SurveyDuration;
import com.example.surveyapi.domain.survey.domain.survey.vo.SurveyOption;
import com.example.surveyapi.domain.survey.domain.survey.vo.ChoiceInfo;
import com.example.surveyapi.domain.survey.domain.question.enums.QuestionType;
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
@TestPropertySource(properties = "SECRET_KEY=12345678901234567890123456789012")
@Transactional
class SurveyServiceTest {

    @Autowired
    SurveyService surveyService;
    @Autowired
    SurveyRepository surveyRepository;

    @Test
    @DisplayName("정상 설문 생성 - DB 저장 검증")
    void createSurvey_success() {
        // given
        CreateSurveyRequest request = new CreateSurveyRequest();
        ReflectionTestUtils.setField(request, "title", "설문 제목");
        ReflectionTestUtils.setField(request, "surveyType", SurveyType.VOTE);
        ReflectionTestUtils.setField(request, "surveyDuration", SurveyDuration.of(LocalDateTime.now(), LocalDateTime.now().plusDays(1)));
        ReflectionTestUtils.setField(request, "surveyOption", SurveyOption.of(true, true));
        ReflectionTestUtils.setField(request, "questions", List.of(
                QuestionInfo.of("Q1", QuestionType.SHORT_ANSWER, true, 1, List.of())
        ));

        // when
        Long surveyId = surveyService.create(1L, 1L, request);

        // then
        Survey survey = surveyRepository.findBySurveyIdAndCreatorId(surveyId, 1L).orElseThrow();
        assertThat(survey.getTitle()).isEqualTo("설문 제목");
        assertThat(survey.getType()).isEqualTo(SurveyType.VOTE);
    }

    @Test
    @DisplayName("설문 수정 - 제목, 설명만 변경")
    void updateSurvey_titleAndDescription() {
        // given
        CreateSurveyRequest createRequest = new CreateSurveyRequest();
        ReflectionTestUtils.setField(createRequest, "title", "oldTitle");
        ReflectionTestUtils.setField(createRequest, "surveyType", SurveyType.VOTE);
        ReflectionTestUtils.setField(createRequest, "surveyDuration", SurveyDuration.of(LocalDateTime.now(), LocalDateTime.now().plusDays(1)));
        ReflectionTestUtils.setField(createRequest, "surveyOption", SurveyOption.of(true, true));
        ReflectionTestUtils.setField(createRequest, "questions", List.of());
        Long surveyId = surveyService.create(1L, 1L, createRequest);

        UpdateSurveyRequest updateRequest = new UpdateSurveyRequest();
        ReflectionTestUtils.setField(updateRequest, "title", "newTitle");
        ReflectionTestUtils.setField(updateRequest, "description", "newDesc");

        // when
        String result = surveyService.update(surveyId, 1L, updateRequest);

        // then
        Survey survey = surveyRepository.findBySurveyIdAndCreatorId(surveyId, 1L).orElseThrow();
        assertThat(survey.getTitle()).isEqualTo("newTitle");
        assertThat(survey.getDescription()).isEqualTo("newDesc");
        assertThat(result).contains("수정: 2개");
    }

    @Test
    @DisplayName("설문 삭제 - isDeleted, status 변경")
    void deleteSurvey() {
        // given
        CreateSurveyRequest createRequest = new CreateSurveyRequest();
        ReflectionTestUtils.setField(createRequest, "title", "title");
        ReflectionTestUtils.setField(createRequest, "surveyType", SurveyType.VOTE);
        ReflectionTestUtils.setField(createRequest, "surveyDuration", SurveyDuration.of(LocalDateTime.now(), LocalDateTime.now().plusDays(1)));
        ReflectionTestUtils.setField(createRequest, "surveyOption", SurveyOption.of(true, true));
        ReflectionTestUtils.setField(createRequest, "questions", List.of());
        Long surveyId = surveyService.create(1L, 1L, createRequest);

        // when
        String result = surveyService.delete(surveyId, 1L);

        // then
        Survey survey = surveyRepository.findBySurveyIdAndCreatorId(surveyId, 1L).orElseThrow();
        assertThat(survey.getIsDeleted()).isTrue();
        assertThat(survey.getStatus().name()).isEqualTo("DELETED");
        assertThat(result).contains("설문 삭제");
    }

    @Test
    @DisplayName("설문 조회 - 정상 조회")
    void getSurvey() {
        // given
        CreateSurveyRequest createRequest = new CreateSurveyRequest();
        ReflectionTestUtils.setField(createRequest, "title", "title");
        ReflectionTestUtils.setField(createRequest, "surveyType", SurveyType.VOTE);
        ReflectionTestUtils.setField(createRequest, "surveyDuration", SurveyDuration.of(LocalDateTime.now(), LocalDateTime.now().plusDays(1)));
        ReflectionTestUtils.setField(createRequest, "surveyOption", SurveyOption.of(true, true));
        ReflectionTestUtils.setField(createRequest, "questions", List.of());
        Long surveyId = surveyService.create(1L, 1L, createRequest);

        // when
        Survey survey = surveyRepository.findBySurveyIdAndCreatorId(surveyId, 1L).orElseThrow();

        // then
        assertThat(survey.getTitle()).isEqualTo("title");
        assertThat(survey.getSurveyId()).isEqualTo(surveyId);
    }
} 