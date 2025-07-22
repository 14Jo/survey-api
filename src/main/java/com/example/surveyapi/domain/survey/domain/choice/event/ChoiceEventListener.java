package com.example.surveyapi.domain.survey.domain.choice.event;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.example.surveyapi.domain.survey.application.ChoiceService;
import com.example.surveyapi.domain.survey.domain.question.event.QuestionCreateEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChoiceEventListener {

	private final ChoiceService choiceService;

	@Async
	@EventListener
	public void handleChoiceCreated(QuestionCreateEvent event) {
		try {
			log.info("선택지 생성 호출 - 설문 Id : {}", event.getQuestionId());
			choiceService.create(event.getQuestionId(), event.getChoiceList());
			log.info("선택지 생성 종료");
		} catch (Exception e) {
			log.error("선택지 생성 실패 - message : {}", e.getMessage());
		}
	}

}
