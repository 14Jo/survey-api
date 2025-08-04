package com.example.surveyapi.domain.survey.application;

import com.example.surveyapi.domain.survey.domain.question.Question;
import com.example.surveyapi.domain.survey.domain.question.QuestionOrderService;
import com.example.surveyapi.domain.survey.domain.question.QuestionRepository;
import com.example.surveyapi.domain.survey.domain.question.enums.QuestionType;
import com.example.surveyapi.domain.survey.domain.survey.vo.ChoiceInfo;
import com.example.surveyapi.domain.survey.domain.survey.vo.QuestionInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@Testcontainers
@SpringBootTest
@Transactional
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class QuestionServiceTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private QuestionService questionService;

    @Autowired
    private QuestionRepository questionRepository;

    @MockitoBean
    private QuestionOrderService questionOrderService;

    private List<QuestionInfo> questionInfos;

    @BeforeEach
    void setUp() {
        // given
        questionInfos = List.of(
            QuestionInfo.of("질문1", QuestionType.SHORT_ANSWER, true, 1, List.of()),
            QuestionInfo.of("질문2", QuestionType.MULTIPLE_CHOICE, false, 2,
                List.of(ChoiceInfo.of("선택1", 1), ChoiceInfo.of("선택2", 2)))
        );
    }

    @Test
    @DisplayName("질문 생성 - 성공")
    void createQuestions_success() {
        // given
        Long surveyId = 1L;
        
        // when
        questionService.create(surveyId, questionInfos);

        // then
        List<Question> savedQuestions = questionRepository.findAllBySurveyId(surveyId);
        assertThat(savedQuestions).hasSize(2);
        assertThat(savedQuestions.get(0).getContent()).isEqualTo("질문1");
    }

    @Test
    @DisplayName("질문 생성 - 빈 리스트")
    void createQuestions_emptyList() {
        // given
        Long surveyId = 2L;
        List<QuestionInfo> emptyQuestions = List.of();

        // when
        questionService.create(surveyId, emptyQuestions);

        // then
        List<Question> savedQuestions = questionRepository.findAllBySurveyId(surveyId);
        assertThat(savedQuestions).isEmpty();
    }

    @Test
    @DisplayName("질문 삭제 - 성공 (소프트 삭제)")
    void deleteQuestions_success_softDelete() {
        // given
        Long surveyId = 3L;
        questionService.create(surveyId, questionInfos);

        // when
        questionService.delete(surveyId);

        // then
        List<Question> softDeletedQuestions = questionRepository.findAllBySurveyId(surveyId);
        assertThat(softDeletedQuestions).hasSize(2);
        assertThat(softDeletedQuestions).allMatch(Question::getIsDeleted);
    }

    @Test
    @DisplayName("질문 순서 조정 - 성공")
    void adjustDisplayOrder_success() {
        // given
        Long surveyId = 4L;
        List<QuestionInfo> newQuestions = List.of(
            QuestionInfo.of("새 질문1", QuestionType.SHORT_ANSWER, true, 1, List.of())
        );
        when(questionOrderService.adjustDisplayOrder(surveyId, newQuestions)).thenReturn(newQuestions);

        // when
        List<QuestionInfo> result = questionService.adjustDisplayOrder(surveyId, newQuestions);

        // then
        assertThat(result).isEqualTo(newQuestions);
        verify(questionOrderService).adjustDisplayOrder(surveyId, newQuestions);
    }
}