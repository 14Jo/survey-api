package com.example.surveyapi.domain.survey.domain.question;

import com.example.surveyapi.domain.survey.domain.question.enums.QuestionType;
import com.example.surveyapi.domain.survey.domain.survey.vo.QuestionInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = "SECRET_KEY=12345678901234567890123456789012")
class QuestionOrderServiceTest {

    @Autowired
    QuestionOrderService questionOrderService;

    @Test
    @DisplayName("질문 삽입 - 기존 질문 없을 때 1번부터 순차 할당")
    void adjustDisplayOrder_firstInsert() {
        // given
        List<QuestionInfo> input = List.of(
                new QuestionInfo("Q1", QuestionType.LONG_ANSWER, true, 2, List.of()),
                new QuestionInfo("Q2", QuestionType.SHORT_ANSWER, true, 3, List.of()),
                new QuestionInfo("Q3", QuestionType.SHORT_ANSWER, true, 3, List.of())
        );
        
        // when
        List<QuestionInfo> result = questionOrderService.adjustDisplayOrder(999L, input);
        
        // then
        assertThat(result).extracting("displayOrder").containsExactly(1, 2, 3);
    }

    //TODO 기존 질문이 있을 경우의 테스트도 필요
} 