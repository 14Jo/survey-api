package com.example.surveyapi.domain.project.infra.event;

import java.util.Map;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import com.example.surveyapi.domain.project.application.event.ProjectEventPublisher;
import com.example.surveyapi.global.event.EventCode;
import com.example.surveyapi.global.event.RabbitConst;
import com.example.surveyapi.global.event.project.ProjectEvent;
import com.example.surveyapi.global.exception.CustomErrorCode;
import com.example.surveyapi.global.exception.CustomException;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProjectEventPublisherImpl implements ProjectEventPublisher {

	private final RabbitTemplate rabbitTemplate;
	private Map<EventCode, String> routingKeyMap;

	@PostConstruct
	public void initialize() {
		routingKeyMap = Map.of(
			EventCode.PROJECT_STATE_CHANGED, RabbitConst.ROUTING_KEY_PROJECT_STATE_CHANGED,
			EventCode.PROJECT_DELETED, RabbitConst.ROUTING_KEY_PROJECT_DELETED,
			EventCode.PROJECT_CREATED, RabbitConst.ROUTING_KEY_PROJECT_CREATED
		);
	}

	@Override
	public void convertAndSend(ProjectEvent event) {
		String routingKey = routingKeyMap.get(event.getEventCode());
		if (routingKey == null) {
			throw new CustomException(CustomErrorCode.NOT_FOUND_ROUTING_KEY);
		}
		rabbitTemplate.convertAndSend(RabbitConst.EXCHANGE_NAME, routingKey, event);
	}
}