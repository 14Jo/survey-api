package com.example.surveyapi.domain.survey.infra.question.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.surveyapi.domain.survey.domain.question.Question;

public interface JpaQuestionRepository extends JpaRepository<Question, Integer> {
	List<Question> findBySurveyId(Long surveyId);
}
