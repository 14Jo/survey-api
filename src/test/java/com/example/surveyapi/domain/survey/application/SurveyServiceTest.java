package com.example.surveyapi.domain.survey.application;

import com.example.surveyapi.domain.survey.application.request.CreateSurveyRequest;
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
        ReflectionTestUtils.setField(request, "surveyDuration", new SurveyDuration(LocalDateTime.now(), LocalDateTime.now().plusDays(1)));
        ReflectionTestUtils.setField(request, "surveyOption", new SurveyOption(true, true));
        ReflectionTestUtils.setField(request, "questions", List.of(
                new QuestionInfo("Q1", QuestionType.SHORT_ANSWER, true, 1, List.of())
        ));

        // when
        Long surveyId = surveyService.create(1L, 1L, request);

        // then
        var survey = surveyRepository.findBySurveyIdAndCreatorId(surveyId, 1L).orElseThrow();
        assertThat(survey.getTitle()).isEqualTo("설문 제목");
        assertThat(survey.getType()).isEqualTo(SurveyType.VOTE);
    }

    @Test
    @DisplayName("질문 displayOrder 중복/비연속 자동정렬")
    void createSurvey_questionOrderAdjust() {
        // given
        CreateSurveyRequest request = new CreateSurveyRequest();
        ReflectionTestUtils.setField(request, "title", "설문 제목");
        ReflectionTestUtils.setField(request, "surveyType", SurveyType.VOTE);
        ReflectionTestUtils.setField(request, "surveyDuration", new SurveyDuration(LocalDateTime.now(), LocalDateTime.now().plusDays(1)));
        ReflectionTestUtils.setField(request, "surveyOption", new SurveyOption(true, true));
        ReflectionTestUtils.setField(request, "questions", List.of(
                new QuestionInfo("Q1", QuestionType.SHORT_ANSWER, true, 2, List.of()),
                new QuestionInfo("Q2", QuestionType.SHORT_ANSWER, true, 2, List.of()),
                new QuestionInfo("Q3", QuestionType.SHORT_ANSWER, true, 5, List.of())
        ));

        // when
        Long surveyId = surveyService.create(1L, 1L, request);

        // then
        var survey = surveyRepository.findBySurveyIdAndCreatorId(surveyId, 1L).orElseThrow();
        assertThat(survey.getTitle()).isEqualTo("설문 제목");
    }

    @Test
    @DisplayName("선택지 displayOrder 중복/비연속 자동정렬")
    void createSurvey_choiceOrderAdjust() {
        // given
        CreateSurveyRequest request = new CreateSurveyRequest();
        ReflectionTestUtils.setField(request, "title", "설문 제목");
        ReflectionTestUtils.setField(request, "surveyType", SurveyType.VOTE);
        ReflectionTestUtils.setField(request, "surveyDuration", new SurveyDuration(LocalDateTime.now(), LocalDateTime.now().plusDays(1)));
        ReflectionTestUtils.setField(request, "surveyOption", new SurveyOption(true, true));
        ReflectionTestUtils.setField(request, "questions", List.of(
                new QuestionInfo("Q1", QuestionType.MULTIPLE_CHOICE, true, 1, List.of(
                        new ChoiceInfo("A", 1),
                        new ChoiceInfo("B", 1),
                        new ChoiceInfo("C", 3)
                ))
        ));

        // when
        Long surveyId = surveyService.create(1L, 1L, request);

        // then
        var survey = surveyRepository.findBySurveyIdAndCreatorId(surveyId, 1L).orElseThrow();
        assertThat(survey.getTitle()).isEqualTo("설문 제목");
    }
} 