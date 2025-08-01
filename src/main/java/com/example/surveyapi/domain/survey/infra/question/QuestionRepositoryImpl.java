package com.example.surveyapi.domain.survey.infra.question;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.example.surveyapi.domain.survey.domain.question.Question;
import com.example.surveyapi.domain.survey.domain.question.QuestionRepository;
import com.example.surveyapi.domain.survey.infra.question.jpa.JpaQuestionRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class QuestionRepositoryImpl implements QuestionRepository {

	private final JpaQuestionRepository jpaRepository;

	@Override
	public Question save(Question choice) {
		return jpaRepository.save(choice);
	}

	@Override
	public void saveAll(List<Question> choices) {
		jpaRepository.saveAll(choices);
	}

	@Override
	public List<Question> findAllBySurveyId(Long surveyId) {
		return jpaRepository.findBySurveyId(surveyId);
	}
}
