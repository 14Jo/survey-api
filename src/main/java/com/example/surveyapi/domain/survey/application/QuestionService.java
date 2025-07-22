package com.example.surveyapi.domain.survey.application;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.surveyapi.domain.survey.domain.question.Question;
import com.example.surveyapi.domain.survey.domain.question.QuestionRepository;
import com.example.surveyapi.domain.survey.domain.request.CreateQuestionRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuestionService {

	private final QuestionRepository questionRepository;

	public void create(Long surveyId, List<CreateQuestionRequest> questions) {
		long startTime = System.currentTimeMillis();
		List<Question> questionList = questions.stream()
			.map(question -> Question.create(
				surveyId,
				question.getContent(),
				question.getQuestionType(),
				question.getDisplayOrder(),
				question.isRequired()
			)).toList();

		questionRepository.saveAll(questionList);
		long endTime = System.currentTimeMillis();
		log.info("질문 생성 시간 - 총 {} ms", endTime - startTime);
	}

}
