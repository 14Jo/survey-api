package com.example.surveyapi.survey.domain.survey.vo;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SurveyOptionTest {

    @Test
    @DisplayName("SurveyOption.of - 익명, 응답 수정 가능")
    void createSurveyOption_anonymousAndUpdateAllowed() {
        // when
        SurveyOption option = SurveyOption.of(true, true);
        
        // then
        assertThat(option).isNotNull();
        assertThat(option.isAnonymous()).isTrue();
        assertThat(option.isAllowResponseUpdate()).isTrue();
    }

    @Test
    @DisplayName("SurveyOption.of - 실명, 응답 수정 불가")
    void createSurveyOption_notAnonymousAndUpdateNotAllowed() {
        // when
        SurveyOption option = SurveyOption.of(false, false);
        
        // then
        assertThat(option.isAnonymous()).isFalse();
        assertThat(option.isAllowResponseUpdate()).isFalse();
    }

    @Test
    @DisplayName("SurveyOption.of - 익명이지만 응답 수정 불가")
    void createSurveyOption_anonymousButUpdateNotAllowed() {
        // when
        SurveyOption option = SurveyOption.of(true, false);
        
        // then
        assertThat(option.isAnonymous()).isTrue();
        assertThat(option.isAllowResponseUpdate()).isFalse();
    }

    @Test
    @DisplayName("SurveyOption.of - 실명이지만 응답 수정 가능")
    void createSurveyOption_notAnonymousButUpdateAllowed() {
        // when
        SurveyOption option = SurveyOption.of(false, true);
        
        // then
        assertThat(option.isAnonymous()).isFalse();
        assertThat(option.isAllowResponseUpdate()).isTrue();
    }

    @Test
    @DisplayName("SurveyOption - 필드 값 비교 테스트")
    void surveyOption_fieldComparison() {
        // given
        SurveyOption option1 = SurveyOption.of(true, true);
        SurveyOption option2 = SurveyOption.of(true, true);
        SurveyOption option3 = SurveyOption.of(false, true);
        SurveyOption option4 = SurveyOption.of(true, false);
        
        // then
        assertThat(option1.isAnonymous()).isEqualTo(option2.isAnonymous());
        assertThat(option1.isAllowResponseUpdate()).isEqualTo(option2.isAllowResponseUpdate());
        assertThat(option1.isAnonymous()).isNotEqualTo(option3.isAnonymous());
        assertThat(option1.isAllowResponseUpdate()).isNotEqualTo(option4.isAllowResponseUpdate());
    }
}