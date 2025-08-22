package com.example.surveyapi.domain.survey.domain.question;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.example.surveyapi.domain.survey.domain.question.enums.QuestionType;
import com.example.surveyapi.domain.survey.domain.survey.Survey;
import com.example.surveyapi.domain.survey.domain.survey.enums.SurveyType;
import com.example.surveyapi.domain.survey.domain.survey.vo.ChoiceInfo;
import com.example.surveyapi.domain.survey.domain.survey.vo.SurveyDuration;
import com.example.surveyapi.domain.survey.domain.survey.vo.SurveyOption;
import com.example.surveyapi.global.exception.CustomErrorCode;
import com.example.surveyapi.global.exception.CustomException;

class QuestionTest {

    @Test
    @DisplayName("Question.create - 단답형 질문 생성")
    void createQuestion_shortAnswer_success() {
        // given
        Survey survey = createSampleSurvey();
        
        // when
        Question question = Question.create(
            survey,
            "이름을 입력하세요",
            QuestionType.SHORT_ANSWER,
            1,
            true,
            List.of()
        );
        
        // then
        assertThat(question).isNotNull();
        assertThat(question.getContent()).isEqualTo("이름을 입력하세요");
        assertThat(question.getType()).isEqualTo(QuestionType.SHORT_ANSWER);
        assertThat(question.getDisplayOrder()).isEqualTo(1);
        assertThat(question.isRequired()).isTrue();
        assertThat(question.getChoices()).isEmpty();
        assertThat(question.getSurvey()).isEqualTo(survey);
    }

    @Test
    @DisplayName("Question.create - 장문형 질문 생성")
    void createQuestion_longAnswer_success() {
        // given
        Survey survey = createSampleSurvey();
        
        // when
        Question question = Question.create(
            survey,
            "프로젝트에 대한 의견을 자세히 작성해 주세요",
            QuestionType.LONG_ANSWER,
            2,
            false,
            List.of()
        );
        
        // then
        assertThat(question.getContent()).isEqualTo("프로젝트에 대한 의견을 자세히 작성해 주세요");
        assertThat(question.getType()).isEqualTo(QuestionType.LONG_ANSWER);
        assertThat(question.getDisplayOrder()).isEqualTo(2);
        assertThat(question.isRequired()).isFalse();
        assertThat(question.getChoices()).isEmpty();
    }

    @Test
    @DisplayName("Question.create - 단일 선택형 질문 생성")
    void createQuestion_singleChoice_success() {
        // given
        Survey survey = createSampleSurvey();
        List<ChoiceInfo> choices = List.of(
            ChoiceInfo.of("선택지 1", 1),
            ChoiceInfo.of("선택지 2", 2),
            ChoiceInfo.of("선택지 3", 3)
        );
        
        // when
        Question question = Question.create(
            survey,
            "가장 좋아하는 색깔은?",
            QuestionType.SINGLE_CHOICE,
            1,
            true,
            choices
        );
        
        // then
        assertThat(question.getContent()).isEqualTo("가장 좋아하는 색깔은?");
        assertThat(question.getType()).isEqualTo(QuestionType.SINGLE_CHOICE);
        assertThat(question.getChoices()).hasSize(3);
        assertThat(question.getChoices().get(0).getContent()).isEqualTo("선택지 1");
        assertThat(question.getChoices().get(0).getChoiceId()).isEqualTo(1);
    }

    @Test
    @DisplayName("Question.create - 다중 선택형 질문 생성")
    void createQuestion_multipleChoice_success() {
        // given
        Survey survey = createSampleSurvey();
        List<ChoiceInfo> choices = List.of(
            ChoiceInfo.of("Java", 1),
            ChoiceInfo.of("Python", 2),
            ChoiceInfo.of("JavaScript", 3),
            ChoiceInfo.of("TypeScript", 4)
        );
        
        // when
        Question question = Question.create(
            survey,
            "사용 가능한 프로그래밍 언어를 모두 선택하세요",
            QuestionType.MULTIPLE_CHOICE,
            1,
            false,
            choices
        );
        
        // then
        assertThat(question.getContent()).isEqualTo("사용 가능한 프로그래밍 언어를 모두 선택하세요");
        assertThat(question.getType()).isEqualTo(QuestionType.MULTIPLE_CHOICE);
        assertThat(question.getChoices()).hasSize(4);
        assertThat(question.getChoices().get(2).getContent()).isEqualTo("JavaScript");
    }

    @Test
    @DisplayName("Question.create - 선택지 없는 객관식 질문 (빈 리스트)")
    void createQuestion_choiceTypeWithEmptyChoices() {
        // given
        Survey survey = createSampleSurvey();
        
        // when
        Question question = Question.create(
            survey,
            "선택지가 없는 객관식",
            QuestionType.SINGLE_CHOICE,
            1,
            true,
            List.of()
        );
        
        // then
        assertThat(question.getType()).isEqualTo(QuestionType.SINGLE_CHOICE);
        assertThat(question.getChoices()).isEmpty();
    }

    @Test
    @DisplayName("Question.create - 필수가 아닌 질문")
    void createQuestion_notRequired() {
        // given
        Survey survey = createSampleSurvey();
        
        // when
        Question question = Question.create(
            survey,
            "선택적 질문",
            QuestionType.SHORT_ANSWER,
            1,
            false,
            List.of()
        );
        
        // then
        assertThat(question.isRequired()).isFalse();
    }

    @Test
    @DisplayName("Question.create - 표시 순서 설정")
    void createQuestion_displayOrder() {
        // given
        Survey survey = createSampleSurvey();
        
        // when
        Question question = Question.create(
            survey,
            "세 번째 질문",
            QuestionType.SHORT_ANSWER,
            3,
            true,
            List.of()
        );
        
        // then
        assertThat(question.getDisplayOrder()).isEqualTo(3);
    }

    @Test
    @DisplayName("Question.create - null 선택지 리스트로 인한 예외")
    void createQuestion_nullChoices_throwsException() {
        // given
        Survey survey = createSampleSurvey();
        
        // when & then
        assertThatThrownBy(() -> Question.create(
            survey,
            "null 선택지 질문",
            QuestionType.SINGLE_CHOICE,
            1,
            true,
            null
        ))
        .isInstanceOf(CustomException.class)
        .hasFieldOrPropertyWithValue("errorCode", CustomErrorCode.SERVER_ERROR);
    }

    private Survey createSampleSurvey() {
        return Survey.create(
            1L, 1L, "테스트 설문", "설명",
            SurveyType.SURVEY,
            SurveyDuration.of(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(10)),
            SurveyOption.of(true, false),
            List.of()
        );
    }
}