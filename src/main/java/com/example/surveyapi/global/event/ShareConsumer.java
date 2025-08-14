package com.example.surveyapi.global.event;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.example.surveyapi.global.constant.RabbitConst;
import com.example.surveyapi.global.model.SurveyEvent;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RabbitListener(
	queues = RabbitConst.QUEUE_NAME_SHARE
)
public class ShareConsumer {

	@RabbitHandler
	public void handleSurveyEventBatch(SurveyEvent event) {
		try {
			log.info("Received survey event");
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
}
