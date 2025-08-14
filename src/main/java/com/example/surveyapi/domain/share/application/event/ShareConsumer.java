package com.example.surveyapi.domain.share.application.event;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.example.surveyapi.domain.share.application.share.ShareService;
import com.example.surveyapi.domain.share.domain.share.vo.ShareSourceType;
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
	private final ShareService shareService;

	@RabbitHandler
	public void handleSurveyEventBatch(SurveyActivateEvent event) {
		try {
			log.info("Received survey event");

			shareService.createShare(
				ShareSourceType.SURVEY,
				event.getSurveyId(),
				event.getCreatorID(),
				event.getEndTime()
			);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
}
