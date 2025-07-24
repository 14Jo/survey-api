package com.example.surveyapi.domain.survey.domain;

import com.example.surveyapi.domain.survey.domain.survey.Survey;
import com.example.surveyapi.domain.survey.domain.survey.enums.SurveyType;
import com.example.surveyapi.domain.survey.domain.survey.vo.SurveyDuration;
import com.example.surveyapi.domain.survey.domain.survey.vo.SurveyOption;
import com.example.surveyapi.global.exception.CustomException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

class SurveyTest {

    @Test
    @DisplayName("Survey.create - 정상 생성")
    void createSurvey_success() {
        // given
        
        // when
        Survey survey = Survey.create(
                1L, 1L, "title", "desc", SurveyType.VOTE,
                new SurveyDuration(LocalDateTime.now(), LocalDateTime.now().plusDays(1)),
                new SurveyOption(true, true),
                List.of() // questions
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
                null, null, null, null, null, null, null, null
        )).isInstanceOf(CustomException.class); // 실제 예외 타입에 맞게 수정
    }
} 