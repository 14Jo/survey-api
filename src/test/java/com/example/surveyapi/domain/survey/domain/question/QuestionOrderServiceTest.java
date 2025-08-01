package com.example.surveyapi.domain.survey.domain.question;

import com.example.surveyapi.domain.survey.domain.question.enums.QuestionType;
import com.example.surveyapi.domain.survey.domain.survey.vo.ChoiceInfo;
import com.example.surveyapi.domain.survey.domain.survey.vo.QuestionInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuestionOrderServiceTest {

    @Mock
    private QuestionRepository questionRepository;

    @InjectMocks
    private QuestionOrderService questionOrderService;

    private List<QuestionInfo> inputQuestions;
    private List<Question> mockQuestions;

    @BeforeEach
    void setUp() {
        // given
        inputQuestions = new ArrayList<>();
        inputQuestions.add(QuestionInfo.of("질문1", QuestionType.SHORT_ANSWER, true, 2, new ArrayList<>()));
        inputQuestions.add(QuestionInfo.of("질문2", QuestionType.MULTIPLE_CHOICE, false, 2, new ArrayList<>()));
        inputQuestions.add(QuestionInfo.of("질문3", QuestionType.SHORT_ANSWER, true, 5, new ArrayList<>()));

        mockQuestions = new ArrayList<>();
        mockQuestions.add(Question.create(1L, "질문1", QuestionType.SHORT_ANSWER, 1, true, new ArrayList<>()));
        mockQuestions.add(Question.create(1L, "질문2", QuestionType.MULTIPLE_CHOICE, 2, false, new ArrayList<>()));
        mockQuestions.add(Question.create(1L, "질문3", QuestionType.SHORT_ANSWER, 3, true, new ArrayList<>()));
    }

    @Test
    @DisplayName("질문 순서 조정 - 중복 순서 정규화")
    void adjustDisplayOrder_duplicateOrder() {
        // given
        when(questionRepository.findAllBySurveyId(1L)).thenReturn(mockQuestions);

        // when
        List<QuestionInfo> result = questionOrderService.adjustDisplayOrder(1L, inputQuestions);

        // then
        assertThat(result).hasSize(3);
        assertThat(result.get(0).getDisplayOrder()).isEqualTo(2);
        assertThat(result.get(1).getDisplayOrder()).isEqualTo(2);
        assertThat(result.get(2).getDisplayOrder()).isEqualTo(5);
        verify(questionRepository).findAllBySurveyId(1L);
        verify(questionRepository).saveAll(anyList());
    }

    @Test
    @DisplayName("질문 순서 조정 - 비연속 순서 정규화")
    void adjustDisplayOrder_nonConsecutiveOrder() {
        // given
        List<QuestionInfo> nonConsecutiveQuestions = new ArrayList<>();
        nonConsecutiveQuestions.add(QuestionInfo.of("질문1", QuestionType.SHORT_ANSWER, true, 1, new ArrayList<>()));
        nonConsecutiveQuestions.add(QuestionInfo.of("질문2", QuestionType.MULTIPLE_CHOICE, false, 5, new ArrayList<>()));
        nonConsecutiveQuestions.add(QuestionInfo.of("질문3", QuestionType.SHORT_ANSWER, true, 10, new ArrayList<>()));
        when(questionRepository.findAllBySurveyId(1L)).thenReturn(mockQuestions);

        // when
        List<QuestionInfo> result = questionOrderService.adjustDisplayOrder(1L, nonConsecutiveQuestions);

        // then
        assertThat(result).hasSize(3);
        assertThat(result.get(0).getDisplayOrder()).isEqualTo(1);
        assertThat(result.get(1).getDisplayOrder()).isEqualTo(5);
        assertThat(result.get(2).getDisplayOrder()).isEqualTo(10);
        verify(questionRepository).saveAll(anyList());
    }

    @Test
    @DisplayName("질문 순서 조정 - 빈 리스트")
    void adjustDisplayOrder_emptyList() {
        // given
        List<QuestionInfo> emptyQuestions = new ArrayList<>();

        // when
        List<QuestionInfo> result = questionOrderService.adjustDisplayOrder(1L, emptyQuestions);

        // then
        assertThat(result).isEmpty();
        verify(questionRepository, never()).findAllBySurveyId(anyLong());
        verify(questionRepository, never()).saveAll(anyList());
    }

    @Test
    @DisplayName("질문 순서 조정 - null 리스트")
    void adjustDisplayOrder_nullList() {
        // given

        // when
        List<QuestionInfo> result = questionOrderService.adjustDisplayOrder(1L, null);

        // then
        assertThat(result).isEmpty();
        verify(questionRepository, never()).findAllBySurveyId(anyLong());
        verify(questionRepository, never()).saveAll(anyList());
    }

    @Test
    @DisplayName("질문 순서 조정 - 기존 질문이 없는 경우")
    void adjustDisplayOrder_noExistingQuestions() {
        // given
        when(questionRepository.findAllBySurveyId(1L)).thenReturn(new ArrayList<>());

        // when
        List<QuestionInfo> result = questionOrderService.adjustDisplayOrder(1L, inputQuestions);

        // then
        assertThat(result).hasSize(3);
        assertThat(result.get(0).getDisplayOrder()).isEqualTo(1);
        assertThat(result.get(1).getDisplayOrder()).isEqualTo(2);
        assertThat(result.get(2).getDisplayOrder()).isEqualTo(3);
        verify(questionRepository).findAllBySurveyId(1L);
        verify(questionRepository, never()).saveAll(anyList());
    }

    @Test
    @DisplayName("질문 순서 조정 - 기존 질문 순서 업데이트")
    void adjustDisplayOrder_existingQuestionsOrderUpdate() {
        // given
        List<Question> existingQuestions = new ArrayList<>();
        Question existingQuestion1 = Question.create(1L, "기존 질문1", QuestionType.SHORT_ANSWER, 1, true, new ArrayList<>());
        Question existingQuestion2 = Question.create(1L, "기존 질문2", QuestionType.MULTIPLE_CHOICE, 2, false, new ArrayList<>());
        existingQuestions.add(existingQuestion1);
        existingQuestions.add(existingQuestion2);
        
        when(questionRepository.findAllBySurveyId(1L)).thenReturn(existingQuestions);

        List<QuestionInfo> newQuestions = new ArrayList<>();
        newQuestions.add(QuestionInfo.of("새 질문1", QuestionType.SHORT_ANSWER, true, 1, new ArrayList<>()));

        // when
        List<QuestionInfo> result = questionOrderService.adjustDisplayOrder(1L, newQuestions);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDisplayOrder()).isEqualTo(1);
        verify(questionRepository).findAllBySurveyId(1L);
        verify(questionRepository).saveAll(anyList());
    }

    @Test
    @DisplayName("질문 순서 조정 - 선택지 순서도 정규화")
    void adjustDisplayOrder_choiceOrder() {
        // given
        List<QuestionInfo> questionsWithChoices = new ArrayList<>();
        List<ChoiceInfo> choices = new ArrayList<>();
        choices.add(ChoiceInfo.of("선택1", 3));
        choices.add(ChoiceInfo.of("선택2", 3));
        choices.add(ChoiceInfo.of("선택3", 3));
        questionsWithChoices.add(QuestionInfo.of("질문1", QuestionType.MULTIPLE_CHOICE, true, 1, choices));
        when(questionRepository.findAllBySurveyId(1L)).thenReturn(mockQuestions);

        // when
        List<QuestionInfo> result = questionOrderService.adjustDisplayOrder(1L, questionsWithChoices);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getChoices()).hasSize(3);
        assertThat(result.get(0).getChoices().get(0).getDisplayOrder()).isEqualTo(3);
        assertThat(result.get(0).getChoices().get(1).getDisplayOrder()).isEqualTo(3);
        assertThat(result.get(0).getChoices().get(2).getDisplayOrder()).isEqualTo(3);
        verify(questionRepository).saveAll(anyList());
    }

    @Test
    @DisplayName("질문 순서 조정 - 단일 질문")
    void adjustDisplayOrder_singleQuestion() {
        // given
        List<QuestionInfo> singleQuestion = new ArrayList<>();
        singleQuestion.add(QuestionInfo.of("단일 질문", QuestionType.SHORT_ANSWER, true, 1, new ArrayList<>()));
        when(questionRepository.findAllBySurveyId(1L)).thenReturn(mockQuestions);

        // when
        List<QuestionInfo> result = questionOrderService.adjustDisplayOrder(1L, singleQuestion);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDisplayOrder()).isEqualTo(1);
        verify(questionRepository).saveAll(anyList());
    }
} 