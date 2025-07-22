package com.example.surveyapi.domain.survey.application;

import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import com.example.surveyapi.domain.survey.domain.question.Question;
import com.example.surveyapi.domain.survey.domain.question.QuestionRepository;
import com.example.surveyapi.domain.survey.application.request.CreateQuestionRequest;
import com.example.surveyapi.domain.survey.domain.question.event.QuestionCreateEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuestionService {

	private final QuestionRepository questionRepository;
	private final ApplicationEventPublisher eventPublisher;

	//TODO 트랜잭션 관리 방법 생각하기
	//TODO 벌크 인서트 고려하기
	public void create(Long surveyId, List<CreateQuestionRequest> questions) {
		long startTime = System.currentTimeMillis();
		questions.forEach(question -> {
			Question q = Question.create(
				surveyId,
				question.getContent(),
				question.getQuestionType(),
				question.getDisplayOrder(),
				question.isRequired()
			);
			Question save = questionRepository.save(q);
			eventPublisher.publishEvent(new QuestionCreateEvent(save.getQuestionId(), question.getChoices()));
		});
		long endTime = System.currentTimeMillis();
		log.info("질문 생성 시간 - 총 {} ms", endTime - startTime);
	}

}
