package com.example.surveyapi.domain.survey.domain.survey;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.example.surveyapi.domain.survey.domain.question.enums.QuestionType;
import com.example.surveyapi.domain.survey.domain.survey.enums.SurveyStatus;
import com.example.surveyapi.domain.survey.domain.survey.enums.SurveyType;
import com.example.surveyapi.domain.survey.domain.survey.vo.QuestionInfo;
import com.example.surveyapi.domain.survey.domain.survey.vo.SurveyDuration;
import com.example.surveyapi.domain.survey.domain.survey.vo.SurveyOption;

class SurveyTest {

    @Test
    @DisplayName("Survey.create - 정상 생성")
    void createSurvey_success() {
        // given
        LocalDateTime startDate = LocalDateTime.now().plusDays(1);
        LocalDateTime endDate = LocalDateTime.now().plusDays(10);
        List<QuestionInfo> questions = List.of(
            QuestionInfo.of("질문1", QuestionType.SHORT_ANSWER, true, 1, List.of())
        );

        // when
        Survey survey = Survey.create(
            1L, 1L, "설문 제목", "설문 설명", SurveyType.VOTE,
            SurveyDuration.of(startDate, endDate),
            SurveyOption.of(true, true),
            questions
        );

        // then
        assertThat(survey).isNotNull();
        assertThat(survey.getTitle()).isEqualTo("설문 제목");
        assertThat(survey.getDescription()).isEqualTo("설문 설명");
        assertThat(survey.getType()).isEqualTo(SurveyType.VOTE);
        assertThat(survey.getStatus()).isEqualTo(SurveyStatus.PREPARING);
        assertThat(survey.getProjectId()).isEqualTo(1L);
        assertThat(survey.getCreatorId()).isEqualTo(1L);
        assertThat(survey.getDuration().getStartDate()).isEqualTo(startDate);
        assertThat(survey.getDuration().getEndDate()).isEqualTo(endDate);
        assertThat(survey.getOption().isAnonymous()).isTrue();
        assertThat(survey.getOption().isAllowResponseUpdate()).isTrue();
        assertThat(survey.getIsDeleted()).isFalse();
    }

    @Test
    @DisplayName("Survey.create - 빈 questions 리스트 허용")
    void createSurvey_withEmptyQuestions() {
        // given
        LocalDateTime startDate = LocalDateTime.now().plusDays(1);
        LocalDateTime endDate = LocalDateTime.now().plusDays(10);

        // when
        Survey survey = Survey.create(
            1L, 1L, "설문 제목", "설문 설명", SurveyType.VOTE,
            SurveyDuration.of(startDate, endDate),
            SurveyOption.of(true, true),
            List.of()
        );

        // then
        assertThat(survey).isNotNull();
        assertThat(survey.getTitle()).isEqualTo("설문 제목");
        assertThat(survey.getType()).isEqualTo(SurveyType.VOTE);
        assertThat(survey.getStatus()).isEqualTo(SurveyStatus.PREPARING);
        assertThat(survey.getQuestions()).isEmpty();
    }

    @Test
    @DisplayName("Survey.openAt - 준비 중에서 진행 중으로 상태 변경")
    void openSurvey_success() {
        // given
        Survey survey = Survey.create(
            1L, 1L, "설문 제목", "설문 설명", SurveyType.VOTE,
            SurveyDuration.of(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(10)),
            SurveyOption.of(true, true),
            List.of()
        );

        // when
        survey.openAt(LocalDateTime.now());

        // then
        assertThat(survey.getStatus()).isEqualTo(SurveyStatus.IN_PROGRESS);
    }

    @Test
    @DisplayName("Survey.closeAt - 진행 중에서 종료로 상태 변경")
    void closeSurvey_success() {
        // given
        Survey survey = Survey.create(
            1L, 1L, "설문 제목", "설문 설명", SurveyType.VOTE,
            SurveyDuration.of(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(10)),
            SurveyOption.of(true, true),
            List.of()
        );
        survey.openAt(LocalDateTime.now());

        // when
        survey.closeAt(LocalDateTime.now());

        // then
        assertThat(survey.getStatus()).isEqualTo(SurveyStatus.CLOSED);
    }

    @Test
    @DisplayName("Survey.delete - 삭제 상태로 변경")
    void deleteSurvey_success() {
        // given
        Survey survey = Survey.create(
            1L, 1L, "설문 제목", "설문 설명", SurveyType.VOTE,
            SurveyDuration.of(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(10)),
            SurveyOption.of(true, true),
            List.of()
        );

        // when
        survey.delete();

        // then
        assertThat(survey.getIsDeleted()).isTrue();
        assertThat(survey.getStatus()).isEqualTo(SurveyStatus.DELETED);
    }

    @Test
    @DisplayName("Survey.updateFields - 제목과 설명 수정")
    void updateSurvey_titleAndDescription() {
        // given
        Survey survey = Survey.create(
            1L, 1L, "기존 제목", "기존 설명", SurveyType.VOTE,
            SurveyDuration.of(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(10)),
            SurveyOption.of(true, true),
            List.of()
        );

        // when
        survey.updateFields(Map.of(
            "title", "수정된 제목",
            "description", "수정된 설명"
        ));

        // then
        assertThat(survey.getTitle()).isEqualTo("수정된 제목");
        assertThat(survey.getDescription()).isEqualTo("수정된 설명");
    }

    @Test
    @DisplayName("Survey.updateFields - 설문 타입 수정")
    void updateSurvey_type() {
        // given
        Survey survey = Survey.create(
            1L, 1L, "제목", "설명", SurveyType.VOTE,
            SurveyDuration.of(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(10)),
            SurveyOption.of(true, true),
            List.of()
        );

        // when
        survey.updateFields(Map.of("type", SurveyType.SURVEY));

        // then
        assertThat(survey.getType()).isEqualTo(SurveyType.SURVEY);
    }

    @Test
    @DisplayName("Survey.updateFields - 설문 기간 수정")
    void updateSurvey_duration() {
        // given
        Survey survey = Survey.create(
            1L, 1L, "제목", "설명", SurveyType.VOTE,
            SurveyDuration.of(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(10)),
            SurveyOption.of(true, true),
            List.of()
        );
        LocalDateTime newStartDate = LocalDateTime.now().plusDays(5);
        LocalDateTime newEndDate = LocalDateTime.now().plusDays(15);

        // when
        survey.updateFields(Map.of("duration", SurveyDuration.of(newStartDate, newEndDate)));

        // then
        assertThat(survey.getDuration().getStartDate()).isEqualTo(newStartDate);
        assertThat(survey.getDuration().getEndDate()).isEqualTo(newEndDate);
    }

    @Test
    @DisplayName("Survey.updateFields - 설문 옵션 수정")
    void updateSurvey_option() {
        // given
        Survey survey = Survey.create(
            1L, 1L, "제목", "설명", SurveyType.VOTE,
            SurveyDuration.of(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(10)),
            SurveyOption.of(true, true),
            List.of()
        );

        // when
        survey.updateFields(Map.of("option", SurveyOption.of(false, false)));

        // then
        assertThat(survey.getOption().isAnonymous()).isFalse();
        assertThat(survey.getOption().isAllowResponseUpdate()).isFalse();
    }

    @Test
    @DisplayName("Survey.updateFields - 질문 수정")
    void updateSurvey_questions() {
        // given
        Survey survey = Survey.create(
            1L, 1L, "제목", "설명", SurveyType.VOTE,
            SurveyDuration.of(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(10)),
            SurveyOption.of(true, true),
            List.of()
        );

        List<QuestionInfo> newQuestions = List.of(
            QuestionInfo.of("새 질문1", QuestionType.SHORT_ANSWER, true, 1, List.of()),
            QuestionInfo.of("새 질문2", QuestionType.MULTIPLE_CHOICE, false, 2, List.of())
        );

        // when
        survey.updateFields(Map.of("questions", newQuestions));

        // then
        assertThat(survey).isNotNull();
        assertThat(survey.getQuestions()).hasSize(2);
    }

    @Test
    @DisplayName("Survey.updateFields - 부분 수정")
    void updateSurvey_partialUpdate() {
        // given
        Survey survey = Survey.create(
            1L, 1L, "기존 제목", "기존 설명", SurveyType.VOTE,
            SurveyDuration.of(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(10)),
            SurveyOption.of(true, true),
            List.of()
        );

        // when
        survey.updateFields(Map.of("title", "수정된 제목만"));

        // then
        assertThat(survey.getTitle()).isEqualTo("수정된 제목만");
        assertThat(survey.getDescription()).isEqualTo("기존 설명");
        assertThat(survey.getType()).isEqualTo(SurveyType.VOTE);
    }

    @Test
    @DisplayName("Survey.openAt - 시작 시간 업데이트")
    void openSurvey_startTimeUpdate() {
        // given
        LocalDateTime originalStartDate = LocalDateTime.now().plusDays(1);
        LocalDateTime originalEndDate = LocalDateTime.now().plusDays(10);
        LocalDateTime actualStartTime = LocalDateTime.now();
        Survey survey = Survey.create(
            1L, 1L, "설문 제목", "설문 설명", SurveyType.VOTE,
            SurveyDuration.of(originalStartDate, originalEndDate),
            SurveyOption.of(true, true),
            List.of()
        );

        // when
        survey.openAt(actualStartTime);

        // then
        assertThat(survey.getStatus()).isEqualTo(SurveyStatus.IN_PROGRESS);
        assertThat(survey.getDuration().getStartDate()).isEqualTo(actualStartTime);
        assertThat(survey.getDuration().getEndDate()).isEqualTo(originalEndDate);
    }

    @Test
    @DisplayName("Survey.closeAt - 종료 시간 업데이트")
    void closeSurvey_endTimeUpdate() {
        // given
        LocalDateTime originalStartDate = LocalDateTime.now().plusDays(1);
        LocalDateTime originalEndDate = LocalDateTime.now().plusDays(10);
        LocalDateTime actualStartTime = LocalDateTime.now();
        LocalDateTime actualEndTime = LocalDateTime.now().plusMinutes(30);
        Survey survey = Survey.create(
            1L, 1L, "설문 제목", "설문 설명", SurveyType.VOTE,
            SurveyDuration.of(originalStartDate, originalEndDate),
            SurveyOption.of(true, true),
            List.of()
        );
        survey.openAt(actualStartTime);

        // when
        survey.closeAt(actualEndTime);

        // then
        assertThat(survey.getStatus()).isEqualTo(SurveyStatus.CLOSED);
        assertThat(survey.getDuration().getStartDate()).isEqualTo(actualStartTime);
        assertThat(survey.getDuration().getEndDate()).isEqualTo(actualEndTime);
    }
}