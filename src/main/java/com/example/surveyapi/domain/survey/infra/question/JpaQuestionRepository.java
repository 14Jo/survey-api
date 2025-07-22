package com.example.surveyapi.domain.survey.infra.question;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.surveyapi.domain.survey.domain.question.Question;

public interface JpaQuestionRepository extends JpaRepository<Question, Integer> {
}
