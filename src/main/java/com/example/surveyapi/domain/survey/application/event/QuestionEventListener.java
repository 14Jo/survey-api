package com.example.surveyapi.domain.survey.application.event;

import java.util.List;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.example.surveyapi.domain.survey.application.QuestionService;
import com.example.surveyapi.domain.survey.domain.survey.event.SurveyCreatedEvent;
import com.example.surveyapi.domain.survey.domain.survey.event.SurveyDeletedEvent;
import com.example.surveyapi.domain.survey.domain.survey.event.SurveyUpdatedEvent;
import com.example.surveyapi.domain.survey.domain.survey.vo.QuestionInfo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class QuestionEventListener {

	private final QuestionService questionService;

	@Async
	@EventListener
	public void handleSurveyCreated(SurveyCreatedEvent event) {
		try {
			log.info("질문 생성 호출 - 설문 Id : {}", event.getSurveyId());

			Long surveyId = event.getSurveyId().get();

			List<QuestionInfo> questionInfos = questionService.adjustDisplayOrder(surveyId, event.getQuestions());
			questionService.create(surveyId, questionInfos);

			log.info("질문 생성 종료");
		} catch (Exception e) {
			log.error("질문 생성 실패 - message : {}", e.getMessage());
		}
	}

	@EventListener
	@TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
	public void handleSurveyDeleted(SurveyDeletedEvent event) {
		try {
			log.info("질문 삭제 호출 - 설문 Id : {}", event.getSurveyId());

			questionService.delete(event.getSurveyId());

			log.info("질문 삭제 종료");
		} catch (Exception e) {
			log.error("질문 삭제 실패 - message : {}", e.getMessage());
		}
	}

	@EventListener
	@TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
	public void handleSurveyUpdated(SurveyUpdatedEvent event) {
		try {
			log.info("질문 추가 호출 - 설문 Id : {}", event.getSurveyId());

			Long surveyId = event.getSurveyId();

			List<QuestionInfo> questionInfos = questionService.adjustDisplayOrder(surveyId, event.getQuestions());
			questionService.create(surveyId, questionInfos);

			log.info("질문 추가 종료");
		} catch (Exception e) {
			log.error("질문 추가 실패 - message : {}", e.getMessage());
		}
	}
}