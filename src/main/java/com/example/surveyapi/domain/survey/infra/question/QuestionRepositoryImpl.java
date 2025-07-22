package com.example.surveyapi.domain.survey.infra.question;

import org.springframework.stereotype.Repository;

import com.example.surveyapi.domain.survey.domain.question.QuestionRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class QuestionRepositoryImpl implements QuestionRepository {

	private final JpaQuestionRepository jpaRepository;
}
