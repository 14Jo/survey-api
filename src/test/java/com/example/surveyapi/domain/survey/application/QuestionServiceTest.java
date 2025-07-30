package com.example.surveyapi.domain.survey.application;

import com.example.surveyapi.domain.survey.application.request.CreateSurveyRequest;
import com.example.surveyapi.domain.survey.domain.question.Question;
import com.example.surveyapi.domain.survey.domain.question.QuestionRepository;
import com.example.surveyapi.domain.survey.domain.question.enums.QuestionType;
import com.example.surveyapi.domain.survey.domain.survey.enums.SurveyType;
import com.example.surveyapi.domain.survey.domain.survey.vo.ChoiceInfo;
import com.example.surveyapi.domain.survey.domain.survey.vo.QuestionInfo;
import com.example.surveyapi.domain.survey.domain.survey.vo.SurveyDuration;
import com.example.surveyapi.domain.survey.domain.survey.vo.SurveyOption;
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
class QuestionServiceTest {

    @Autowired
    SurveyService surveyService;
    @Autowired
    QuestionRepository questionRepository;

    @Test
    @DisplayName("질문 displayOrder 중복/비연속 삽입 - 중복 없이 저장 검증")
    void createSurvey_questionOrderAdjust() throws Exception {
        // given
        List<QuestionInfo> inputQuestions = List.of(
                QuestionInfo.of("Q1", QuestionType.SHORT_ANSWER, true, 2, List.of()),
                QuestionInfo.of("Q2", QuestionType.SHORT_ANSWER, true, 2, List.of()),
                QuestionInfo.of("Q3", QuestionType.SHORT_ANSWER, true, 5, List.of())
        );
        CreateSurveyRequest request = new CreateSurveyRequest();
        ReflectionTestUtils.setField(request, "title", "설문 제목");
        ReflectionTestUtils.setField(request, "surveyType", SurveyType.VOTE);
        ReflectionTestUtils.setField(request, "surveyDuration", SurveyDuration.of(LocalDateTime.now(), LocalDateTime.now().plusDays(1)));
        ReflectionTestUtils.setField(request, "surveyOption", SurveyOption.of(true, true));
        ReflectionTestUtils.setField(request, "questions", inputQuestions);

        // when
        Long surveyId = surveyService.create(1L, 1L, request);

        // then
        Thread.sleep(200);
        List<Question> savedQuestions = questionRepository.findAllBySurveyId(surveyId);
        assertThat(savedQuestions).hasSize(inputQuestions.size());
        List<Integer> displayOrders = savedQuestions.stream().map(Question::getDisplayOrder).toList();
        assertThat(displayOrders).doesNotHaveDuplicates();
        assertThat(displayOrders).containsExactlyInAnyOrder(1, 2, 3);
    }

    @Test
    @DisplayName("선택지 displayOrder 중복/비연속 삽입 - 중복 없이 저장 검증")
    void createSurvey_choiceOrderAdjust() throws Exception {
        // given
        List<ChoiceInfo> choices = List.of(
                ChoiceInfo.of("A", 3),
                ChoiceInfo.of("B", 3),
                ChoiceInfo.of("C", 3)
        );
        List<QuestionInfo> inputQuestions = List.of(
                QuestionInfo.of("Q1", QuestionType.MULTIPLE_CHOICE, true, 1, choices)
        );
        CreateSurveyRequest request = new CreateSurveyRequest();
        ReflectionTestUtils.setField(request, "title", "설문 제목");
        ReflectionTestUtils.setField(request, "surveyType", SurveyType.VOTE);
        ReflectionTestUtils.setField(request, "surveyDuration", SurveyDuration.of(LocalDateTime.now(), LocalDateTime.now().plusDays(1)));
        ReflectionTestUtils.setField(request, "surveyOption", SurveyOption.of(true, true));
        ReflectionTestUtils.setField(request, "questions", inputQuestions);

        // when
        Long surveyId = surveyService.create(1L, 1L, request);

        // then
        Thread.sleep(200);
        List<Question> savedQuestions = questionRepository.findAllBySurveyId(surveyId);
        assertThat(savedQuestions).hasSize(1);
        Question saved = savedQuestions.get(0);
        List<Integer> choiceOrders = saved.getChoices().stream().map(c -> c.getDisplayOrder()).toList();
        assertThat(choiceOrders).doesNotHaveDuplicates();
        assertThat(choiceOrders).containsExactlyInAnyOrder(3, 4, 5);
    }
} 