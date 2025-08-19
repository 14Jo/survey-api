package com.example.surveyapi.domain.survey.domain.survey.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.example.surveyapi.global.enums.CustomErrorCode;
import com.example.surveyapi.global.exception.CustomException;

class SurveyDurationTest {

    @Test
    @DisplayName("SurveyDuration.of - 정상 생성")
    void createSurveyDuration_success() {
        // given
        LocalDateTime startDate = LocalDateTime.of(2025, 1, 1, 9, 0);
        LocalDateTime endDate = LocalDateTime.of(2025, 1, 31, 18, 0);
        
        // when
        SurveyDuration duration = SurveyDuration.of(startDate, endDate);
        
        // then
        assertThat(duration).isNotNull();
        assertThat(duration.getStartDate()).isEqualTo(startDate);
        assertThat(duration.getEndDate()).isEqualTo(endDate);
    }

    @Test
    @DisplayName("SurveyDuration.of - 시작일만 있는 경우")
    void createSurveyDuration_startDateOnly() {
        // given
        LocalDateTime startDate = LocalDateTime.of(2025, 1, 1, 9, 0);
        
        // when
        SurveyDuration duration = SurveyDuration.of(startDate, null);
        
        // then
        assertThat(duration.getStartDate()).isEqualTo(startDate);
        assertThat(duration.getEndDate()).isNull();
    }

    @Test
    @DisplayName("SurveyDuration.of - 종료일만 있는 경우")
    void createSurveyDuration_endDateOnly() {
        // given
        LocalDateTime endDate = LocalDateTime.of(2025, 1, 31, 18, 0);
        
        // when
        SurveyDuration duration = SurveyDuration.of(null, endDate);
        
        // then
        assertThat(duration.getStartDate()).isNull();
        assertThat(duration.getEndDate()).isEqualTo(endDate);
    }

    @Test
    @DisplayName("SurveyDuration.of - 둘 다 null인 경우")
    void createSurveyDuration_bothNull() {
        // when
        SurveyDuration duration = SurveyDuration.of(null, null);
        
        // then
        assertThat(duration.getStartDate()).isNull();
        assertThat(duration.getEndDate()).isNull();
    }

    @Test
    @DisplayName("SurveyDuration - 같은 시간 설정")
    void createSurveyDuration_sameDateTime() {
        // given
        LocalDateTime sameTime = LocalDateTime.of(2025, 1, 15, 12, 0);
        
        // when
        SurveyDuration duration = SurveyDuration.of(sameTime, sameTime);
        
        // then
        assertThat(duration.getStartDate()).isEqualTo(sameTime);
        assertThat(duration.getEndDate()).isEqualTo(sameTime);
    }

    @Test
    @DisplayName("SurveyDuration - 시작일이 종료일보다 늦은 경우 (허용)")
    void createSurveyDuration_startAfterEnd() {
        // given - 비즈니스 로직에서 검증하므로 VO에서는 단순 생성만 담당
        LocalDateTime startDate = LocalDateTime.of(2025, 1, 31, 18, 0);
        LocalDateTime endDate = LocalDateTime.of(2025, 1, 1, 9, 0);
        
        // when
        SurveyDuration duration = SurveyDuration.of(startDate, endDate);
        
        // then - VO는 단순히 값을 저장만 함
        assertThat(duration.getStartDate()).isEqualTo(startDate);
        assertThat(duration.getEndDate()).isEqualTo(endDate);
    }

    @Test
    @DisplayName("SurveyDuration - 필드 값 비교 테스트")
    void surveyDuration_fieldComparison() {
        // given
        LocalDateTime startDate = LocalDateTime.of(2025, 1, 1, 9, 0);
        LocalDateTime endDate = LocalDateTime.of(2025, 1, 31, 18, 0);
        
        SurveyDuration duration1 = SurveyDuration.of(startDate, endDate);
        SurveyDuration duration2 = SurveyDuration.of(startDate, endDate);
        SurveyDuration duration3 = SurveyDuration.of(startDate, endDate.plusDays(1));
        
        // then - 필드 값으로 비교
        assertThat(duration1.getStartDate()).isEqualTo(duration2.getStartDate());
        assertThat(duration1.getEndDate()).isEqualTo(duration2.getEndDate());
        assertThat(duration1.getEndDate()).isNotEqualTo(duration3.getEndDate());
    }

    @Test
    @DisplayName("SurveyDuration - null 값들을 포함한 필드 비교 테스트")
    void surveyDuration_fieldComparisonWithNulls() {
        // given
        LocalDateTime startDate = LocalDateTime.of(2025, 1, 1, 9, 0);
        
        SurveyDuration duration1 = SurveyDuration.of(startDate, null);
        SurveyDuration duration2 = SurveyDuration.of(startDate, null);
        SurveyDuration duration3 = SurveyDuration.of(null, null);
        SurveyDuration duration4 = SurveyDuration.of(null, null);
        
        // then - 필드 값으로 비교
        assertThat(duration1.getStartDate()).isEqualTo(duration2.getStartDate());
        assertThat(duration1.getEndDate()).isEqualTo(duration2.getEndDate());
        assertThat(duration3.getStartDate()).isEqualTo(duration4.getStartDate());
        assertThat(duration3.getEndDate()).isEqualTo(duration4.getEndDate());
        assertThat(duration1.getStartDate()).isNotEqualTo(duration3.getStartDate());
    }
}