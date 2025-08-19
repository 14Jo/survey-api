package com.example.surveyapi.domain.project.infra.event;

import java.util.Map;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import com.example.surveyapi.domain.project.application.event.ProjectEventPublisher;
import com.example.surveyapi.global.constant.RabbitConst;
import com.example.surveyapi.global.enums.CustomErrorCode;
import com.example.surveyapi.global.enums.EventCode;
import com.example.surveyapi.global.exception.CustomException;
import com.example.surveyapi.global.model.ProjectEvent;

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
			EventCode.PROJECT_ADD_MANAGER, RabbitConst.ROUTING_KEY_ADD_MANAGER,
			EventCode.PROJECT_ADD_MEMBER, RabbitConst.ROUTING_KEY_ADD_MEMBER
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