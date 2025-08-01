package com.example.surveyapi.domain.survey.application;

import com.example.surveyapi.domain.survey.domain.question.Question;
import com.example.surveyapi.domain.survey.domain.question.QuestionOrderService;
import com.example.surveyapi.domain.survey.domain.question.QuestionRepository;
import com.example.surveyapi.domain.survey.domain.question.enums.QuestionType;
import com.example.surveyapi.domain.survey.domain.question.vo.Choice;
import com.example.surveyapi.domain.survey.domain.survey.vo.ChoiceInfo;
import com.example.surveyapi.domain.survey.domain.survey.vo.QuestionInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuestionServiceTest {

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private QuestionOrderService questionOrderService;

    @InjectMocks
    private QuestionService questionService;

    private List<QuestionInfo> questionInfos;
    private List<Question> mockQuestions;

    @BeforeEach
    void setUp() {
        // given
        questionInfos = List.of(
            QuestionInfo.of("질문1", QuestionType.SHORT_ANSWER, true, 1, List.of()),
            QuestionInfo.of("질문2", QuestionType.MULTIPLE_CHOICE, false, 2, 
                List.of(ChoiceInfo.of("선택1", 1), ChoiceInfo.of("선택2", 2)))
        );

        mockQuestions = List.of(
            Question.create(1L, "질문1", QuestionType.SHORT_ANSWER, 1, true, List.of()),
            Question.create(1L, "질문2", QuestionType.MULTIPLE_CHOICE, 2, false, 
                List.of(Choice.of("선택1", 1), Choice.of("선택2", 2)))
        );
    }

    @Test
    @DisplayName("질문 생성 - 성공")
    void createQuestions_success() {
        // given

        // when
        questionService.create(1L, questionInfos);

        // then
        verify(questionRepository).saveAll(anyList());
        verify(questionRepository).saveAll(argThat(questions -> 
            questions.size() == 2 &&
            questions.get(0).getContent().equals("질문1") &&
            questions.get(1).getContent().equals("질문2")
        ));
    }

    @Test
    @DisplayName("질문 생성 - 빈 리스트")
    void createQuestions_emptyList() {
        // given
        List<QuestionInfo> emptyQuestions = List.of();

        // when
        questionService.create(1L, emptyQuestions);

        // then
        verify(questionRepository).saveAll(anyList());
        verify(questionRepository).saveAll(argThat(questions -> questions.isEmpty()));
    }

    @Test
    @DisplayName("질문 생성 - 단일 선택 질문")
    void createQuestions_singleChoice() {
        // given
        List<QuestionInfo> singleChoiceQuestions = List.of(
            QuestionInfo.of("단일 선택 질문", QuestionType.SINGLE_CHOICE, true, 1,
                List.of(ChoiceInfo.of("선택1", 1), ChoiceInfo.of("선택2", 2)))
        );

        // when
        questionService.create(1L, singleChoiceQuestions);

        // then
        verify(questionRepository).saveAll(anyList());
        verify(questionRepository).saveAll(argThat(questions -> 
            questions.size() == 1 &&
            questions.get(0).getType() == QuestionType.SINGLE_CHOICE
        ));
    }

    @Test
    @DisplayName("질문 삭제 - 성공")
    void deleteQuestions_success() {
        // given
        when(questionRepository.findAllBySurveyId(1L)).thenReturn(mockQuestions);

        // when
        questionService.delete(1L);

        // then
        verify(questionRepository).findAllBySurveyId(1L);
        verify(questionRepository).findAllBySurveyId(1L);
    }

    @Test
    @DisplayName("질문 삭제 - 빈 목록")
    void deleteQuestions_emptyList() {
        // given
        when(questionRepository.findAllBySurveyId(1L)).thenReturn(List.of());

        // when
        questionService.delete(1L);

        // then
        verify(questionRepository).findAllBySurveyId(1L);
    }

    @Test
    @DisplayName("질문 삭제 - 존재하지 않는 설문")
    void deleteQuestions_notFound() {
        // given
        when(questionRepository.findAllBySurveyId(999L)).thenReturn(List.of());

        // when
        questionService.delete(999L);

        // then
        verify(questionRepository).findAllBySurveyId(999L);
    }

    @Test
    @DisplayName("질문 순서 조정 - 성공")
    void adjustDisplayOrder_success() {
        // given
        List<QuestionInfo> newQuestions = List.of(
            QuestionInfo.of("새 질문1", QuestionType.SHORT_ANSWER, true, 3, List.of()),
            QuestionInfo.of("새 질문2", QuestionType.MULTIPLE_CHOICE, false, 4, List.of())
        );
        when(questionOrderService.adjustDisplayOrder(1L, newQuestions)).thenReturn(newQuestions);

        // when
        List<QuestionInfo> result = questionService.adjustDisplayOrder(1L, newQuestions);

        // then
        assertThat(result).isEqualTo(newQuestions);
        verify(questionOrderService).adjustDisplayOrder(1L, newQuestions);
    }

    @Test
    @DisplayName("질문 순서 조정 - 빈 리스트")
    void adjustDisplayOrder_emptyList() {
        // given
        List<QuestionInfo> emptyQuestions = List.of();
        when(questionOrderService.adjustDisplayOrder(1L, emptyQuestions)).thenReturn(List.of());

        // when
        List<QuestionInfo> result = questionService.adjustDisplayOrder(1L, emptyQuestions);

        // then
        assertThat(result).isEmpty();
        verify(questionOrderService).adjustDisplayOrder(1L, emptyQuestions);
    }

    @Test
    @DisplayName("질문 순서 조정 - null 리스트")
    void adjustDisplayOrder_nullList() {
        // given
        when(questionOrderService.adjustDisplayOrder(1L, null)).thenReturn(List.of());

        // when
        List<QuestionInfo> result = questionService.adjustDisplayOrder(1L, null);

        // then
        assertThat(result).isEmpty();
        verify(questionOrderService).adjustDisplayOrder(1L, null);
    }
} 