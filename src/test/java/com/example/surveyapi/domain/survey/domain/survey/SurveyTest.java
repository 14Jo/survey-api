package com.example.surveyapi.domain.survey.domain.survey;

import com.example.surveyapi.domain.survey.domain.survey.enums.SurveyStatus;
import com.example.surveyapi.domain.survey.domain.survey.enums.SurveyType;
import com.example.surveyapi.domain.survey.domain.survey.vo.SurveyDuration;
import com.example.surveyapi.domain.survey.domain.survey.vo.SurveyOption;
import com.example.surveyapi.global.exception.CustomException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

class SurveyTest {

    @Test
    @DisplayName("Survey.create - 정상 생성")
    void createSurvey_success() {
        // given
        
        // when
        Survey survey = Survey.create(
                1L, 1L, "title", "desc", SurveyType.VOTE,
                SurveyDuration.of(LocalDateTime.now(), LocalDateTime.now().plusDays(1)),
                SurveyOption.of(true, true),
                List.of()
        );
        
        // then
        assertThat(survey.getTitle()).isEqualTo("title");
        assertThat(survey.getType()).isEqualTo(SurveyType.VOTE);
    }

    @Test
    @DisplayName("Survey.create - 누락시 예외")
    void createSurvey_fail() {
        // given
        
        // when & then
        assertThatThrownBy(() -> Survey.create(
                null, null, null,
            null, null, null, null, null
        )).isInstanceOf(CustomException.class);
    }

    @Test
    @DisplayName("Survey 상태 변경 - open/close/delete")
    void surveyStatusChange() {
        // given
        Survey survey = Survey.create(
                1L, 1L, "title", "desc", SurveyType.VOTE,
                SurveyDuration.of(LocalDateTime.now(), LocalDateTime.now().plusDays(1)),
                SurveyOption.of(true, true),
                List.of()
        );

        // when
        survey.open();
        // then
        assertThat(survey.getStatus().name()).isEqualTo(SurveyStatus.IN_PROGRESS.name());

        // when
        survey.close();
        // then
        assertThat(survey.getStatus().name()).isEqualTo(SurveyStatus.CLOSED.name());

        // when
        survey.delete();
        // then
        assertThat(survey.getStatus().name()).isEqualTo(SurveyStatus.DELETED.name());
        assertThat(survey.getIsDeleted()).isTrue();
    }

    @Test
    @DisplayName("Survey.updateFields - 필드별 동적 변경 및 이벤트 등록")
    void updateFields_dynamic() {
        // given
        Survey survey = Survey.create(
                1L, 1L, "title", "desc", SurveyType.VOTE,
                SurveyDuration.of(LocalDateTime.now(), LocalDateTime.now().plusDays(1)),
                SurveyOption.of(true, true),
                List.of()
        );
        Map<String, Object> fields = new HashMap<>();
        fields.put("title", "newTitle");
        fields.put("description", "newDesc");
        fields.put("type", SurveyType.SURVEY);
        fields.put("duration", SurveyDuration.of(LocalDateTime.now(), LocalDateTime.now().plusDays(2)));
        fields.put("option", SurveyOption.of(false, false));
        fields.put("questions", List.of());

        // when
        survey.updateFields(fields);

        // then
        assertThat(survey.getTitle()).isEqualTo("newTitle");
        assertThat(survey.getDescription()).isEqualTo("newDesc");
        assertThat(survey.getType()).isEqualTo(SurveyType.SURVEY);
        assertThat(survey.getDuration().getEndDate()).isAfter(LocalDateTime.now().plusDays(1));
        assertThat(survey.getOption().isAnonymous()).isFalse();
        assertThat(survey.getUpdatedEvent()).isNotNull();
    }

    @Test
    @DisplayName("Survey.updateFields - 잘못된 필드명 무시")
    void updateFields_ignoreInvalidKey() {
        // given
        Survey survey = Survey.create(
                1L, 1L, "title", "desc", SurveyType.VOTE,
                SurveyDuration.of(LocalDateTime.now(), LocalDateTime.now().plusDays(1)),
                SurveyOption.of(true, true),
                List.of()
        );
        Map<String, Object> fields = new HashMap<>();
        fields.put("testKey", "value");

        // when
        survey.updateFields(fields);

        // then
        assertThat(survey.getTitle()).isEqualTo("title");
    }

    @Test
    @DisplayName("Survey 이벤트 등록/초기화/예외")
    void eventRegisterAndClear() {
        // given
        Survey survey = Survey.create(
                1L, 1L, "title", "desc", SurveyType.VOTE,
                SurveyDuration.of(LocalDateTime.now(), LocalDateTime.now().plusDays(1)),
                SurveyOption.of(true, true),
                List.of()
        );
        ReflectionTestUtils.setField(survey, "surveyId", 1L);

        // when
        survey.registerCreatedEvent();
        // then
        assertThat(survey.getCreatedEvent()).isNotNull();
        survey.clearCreatedEvent();
        assertThatThrownBy(survey::getCreatedEvent).isInstanceOf(CustomException.class);

        // when
        survey.registerDeletedEvent();
        // then
        assertThat(survey.getDeletedEvent()).isNotNull();
        survey.clearDeletedEvent();
        assertThatThrownBy(survey::getDeletedEvent).isInstanceOf(CustomException.class);

        // when
        survey.registerUpdatedEvent(List.of());
        // then
        assertThat(survey.getUpdatedEvent()).isNotNull();
        survey.clearUpdatedEvent();
        assertThat(survey.getUpdatedEvent()).isNull();
    }
} 