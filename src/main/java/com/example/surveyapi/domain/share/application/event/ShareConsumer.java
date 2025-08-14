package com.example.surveyapi.domain.share.application.event;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.example.surveyapi.domain.share.application.event.dto.ShareCreateRequest;
import com.example.surveyapi.domain.share.application.event.port.ShareEventPort;
import com.example.surveyapi.global.constant.RabbitConst;
import com.example.surveyapi.global.event.SurveyActivateEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
@RabbitListener(
	queues = RabbitConst.QUEUE_NAME_SHARE
)
public class ShareConsumer {
	private final ShareEventPort shareEventPort;

	@RabbitHandler
	public void handleSurveyEventBatch(SurveyActivateEvent event) {
		try {
			log.info("Received survey event");

			ShareCreateRequest request = new ShareCreateRequest(
				event.getSurveyId(),
				event.getCreatorID(),
				event.getEndTime()
			);

			shareEventPort.handleSurveyEvent(request);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
}
