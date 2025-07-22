package com.example.surveyapi.domain.survey.domain.question.event;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import com.example.surveyapi.domain.survey.application.QuestionService;
import com.example.surveyapi.domain.survey.domain.survey.event.SurveyCreatedEvent;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class QuestionEventListener {

	private final QuestionService questionService;

	@Async
	@EventListener
	public void handleQuestionCreated(SurveyCreatedEvent event) {
		try {
			log.info("질문 생성 호출 - 설문 Id : {}", event.getSurveyId());
			questionService.create(event.getSurveyId(), event.getQuestions());
			log.info("질문 생성 종료");
		} catch (Exception e) {
			log.error("질문 생성 실패 - message : {}", e.getMessage());
		}
	}
}
