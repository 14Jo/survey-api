package com.example.surveyapi.domain.survey.application;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.example.surveyapi.domain.survey.domain.question.Question;
import com.example.surveyapi.domain.survey.domain.question.QuestionRepository;
import com.example.surveyapi.domain.survey.application.request.CreateQuestionRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuestionService {

	private final QuestionRepository questionRepository;

	//TODO 벌크 인서트 고려하기
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void create(Long surveyId, List<CreateQuestionRequest> questions) {
		long startTime = System.currentTimeMillis();

		List<Question> questionList = questions.stream().map(question ->
			Question.create(
				surveyId, question.getContent(), question.getQuestionType(),
				question.getDisplayOrder(), question.isRequired(), question.getChoices()
			)
		).toList();
		questionRepository.saveAll(questionList);
		long endTime = System.currentTimeMillis();
		log.info("질문 생성 시간 - 총 {} ms", endTime - startTime);
	}

}
