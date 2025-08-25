package com.example.surveyapi.participation.infra.event;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.example.surveyapi.participation.application.event.ParticipationEventPublisherPort;
import com.example.surveyapi.global.event.EventCode;
import com.example.surveyapi.global.event.RabbitConst;
import com.example.surveyapi.global.event.participation.ParticipationGlobalEvent;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ParticipationEventPublisher implements ParticipationEventPublisherPort {

	private final RabbitTemplate rabbitTemplate;

	@Override
	public void publish(ParticipationGlobalEvent event, EventCode key) {
		if (key.equals(EventCode.PARTICIPATION_CREATED)) {
			rabbitTemplate.convertAndSend(RabbitConst.EXCHANGE_NAME, RabbitConst.ROUTING_KEY_PARTICIPATION_CREATE,
				event);
		} else if (key.equals(EventCode.PARTICIPATION_UPDATED)) {
			rabbitTemplate.convertAndSend(RabbitConst.EXCHANGE_NAME, RabbitConst.ROUTING_KEY_PARTICIPATION_UPDATE,
				event);
		}
	}
}
