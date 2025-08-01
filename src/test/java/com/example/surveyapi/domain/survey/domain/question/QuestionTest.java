package com.example.surveyapi.domain.survey.domain.question;

import com.example.surveyapi.domain.survey.domain.question.enums.QuestionType;
import com.example.surveyapi.domain.survey.domain.question.vo.Choice;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

class QuestionTest {

    @Test
    @DisplayName("Question.create - 정상 생성")
    void createQuestion_success() {
        // given
        List<Choice> choices = List.of(
            Choice.of("선택1", 1),
            Choice.of("선택2", 2)
        );

        // when
        Question question = Question.create(
            1L, "질문 내용", QuestionType.MULTIPLE_CHOICE, 1, true, choices
        );

        // then
        assertThat(question).isNotNull();
        assertThat(question.getSurveyId()).isEqualTo(1L);
        assertThat(question.getContent()).isEqualTo("질문 내용");
        assertThat(question.getType()).isEqualTo(QuestionType.MULTIPLE_CHOICE);
        assertThat(question.getDisplayOrder()).isEqualTo(1);
        assertThat(question.isRequired()).isTrue();
        assertThat(question.getChoices()).hasSize(2);
        assertThat(question.getChoices().get(0).getContent()).isEqualTo("선택1");
        assertThat(question.getChoices().get(1).getContent()).isEqualTo("선택2");
    }

    @Test
    @DisplayName("Question.create - 단답형 질문")
    void createQuestion_shortAnswer() {
        // given

        // when
        Question question = Question.create(
            1L, "단답형 질문", QuestionType.SHORT_ANSWER, 1, false, List.of()
        );

        // then
        assertThat(question).isNotNull();
        assertThat(question.getType()).isEqualTo(QuestionType.SHORT_ANSWER);
        assertThat(question.getChoices()).isEmpty();
        assertThat(question.isRequired()).isFalse();
    }

    @Test
    @DisplayName("Question.create - 단일 선택 질문")
    void createQuestion_singleChoice() {
        // given
        List<Choice> choices = List.of(
            Choice.of("선택1", 1),
            Choice.of("선택2", 2),
            Choice.of("선택3", 3)
        );

        // when
        Question question = Question.create(
            1L, "단일 선택 질문", QuestionType.SINGLE_CHOICE, 2, true, choices
        );

        // then
        assertThat(question).isNotNull();
        assertThat(question.getType()).isEqualTo(QuestionType.SINGLE_CHOICE);
        assertThat(question.getChoices()).hasSize(3);
        assertThat(question.getDisplayOrder()).isEqualTo(2);
    }

    @Test
    @DisplayName("Question.create - null choices 허용")
    void createQuestion_withNullChoices() {
        // given

        // when
        Question question = Question.create(
            1L, "질문 내용", QuestionType.SHORT_ANSWER, 1, true, null
        );

        // then
        assertThat(question).isNotNull();
        assertThat(question.getChoices()).isEmpty();
    }

    @Test
    @DisplayName("Question.duplicateChoiceOrder - 중복 순서 처리")
    void duplicateChoiceOrder_success() {
        // given
        List<Choice> choicesWithDuplicateOrder = List.of(
            Choice.of("선택1", 1),
            Choice.of("선택2", 1), // 중복된 순서
            Choice.of("선택3", 2)
        );

        // when
        Question question = Question.create(
            1L, "질문 내용", QuestionType.MULTIPLE_CHOICE, 1, true, choicesWithDuplicateOrder
        );

        // then
        assertThat(question.getChoices()).hasSize(3);
        assertThat(question.getChoices().get(0).getDisplayOrder()).isEqualTo(1);
        assertThat(question.getChoices().get(1).getDisplayOrder()).isEqualTo(2); // 자동으로 2로 변경
        assertThat(question.getChoices().get(2).getDisplayOrder()).isEqualTo(3); // 자동으로 3으로 변경
    }

    @Test
    @DisplayName("Question.duplicateChoiceOrder - 빈 choices")
    void duplicateChoiceOrder_emptyChoices() {
        // given

        // when
        Question question = Question.create(
            1L, "질문 내용", QuestionType.SHORT_ANSWER, 1, true, List.of()
        );

        // then
        assertThat(question.getChoices()).isEmpty();
    }

    @Test
    @DisplayName("Question.duplicateChoiceOrder - 연속된 중복 순서")
    void duplicateChoiceOrder_consecutiveDuplicates() {
        // given
        List<Choice> choicesWithConsecutiveDuplicates = List.of(
            Choice.of("선택1", 1),
            Choice.of("선택2", 1),
            Choice.of("선택3", 1),
            Choice.of("선택4", 2)
        );

        // when
        Question question = Question.create(
            1L, "질문 내용", QuestionType.MULTIPLE_CHOICE, 1, true, choicesWithConsecutiveDuplicates
        );

        // then
        assertThat(question.getChoices()).hasSize(4);
        assertThat(question.getChoices().get(0).getDisplayOrder()).isEqualTo(1);
        assertThat(question.getChoices().get(1).getDisplayOrder()).isEqualTo(2);
        assertThat(question.getChoices().get(2).getDisplayOrder()).isEqualTo(3);
        assertThat(question.getChoices().get(3).getDisplayOrder()).isEqualTo(4);
    }

    @Test
    @DisplayName("Question - 기본값 확인")
    void question_defaultValues() {
        // given

        // when
        Question question = Question.create(
            1L, "질문 내용", QuestionType.SINGLE_CHOICE, 1, false, List.of()
        );

        // then
        assertThat(question.getType()).isEqualTo(QuestionType.SINGLE_CHOICE);
        assertThat(question.isRequired()).isFalse();
        assertThat(question.getChoices()).isEmpty();
    }

    @Test
    @DisplayName("Question - displayOrder 설정")
    void question_displayOrder() {
        // given

        // when
        Question question = Question.create(
            1L, "질문 내용", QuestionType.SHORT_ANSWER, 5, true, List.of()
        );

        // then
        assertThat(question.getDisplayOrder()).isEqualTo(5);
    }

    @Test
    @DisplayName("Question - required 설정")
    void question_required() {
        // given

        // when
        Question question = Question.create(
            1L, "질문 내용", QuestionType.SHORT_ANSWER, 1, true, List.of()
        );

        // then
        assertThat(question.isRequired()).isTrue();
    }
} 