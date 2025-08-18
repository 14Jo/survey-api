package com.example.surveyapi.domain.survey.application.event;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.example.surveyapi.global.constant.RabbitConst;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RabbitListener(
	queues = RabbitConst.QUEUE_NAME_SURVEY
)
public class SurveyConsumer {

	//TODO 이벤트 객체 변환 및 기능 구현 필요
	@RabbitHandler
	public void handleProjectClosed(Object event) {
		try {
			log.info("이벤트 수신");

		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
}
