package com.example.surveyapi.project.infra.event;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.surveyapi.project.application.event.ProjectEventListenerPort;
import com.example.surveyapi.global.event.RabbitConst;
import com.example.surveyapi.global.event.user.UserWithdrawEvent;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@RabbitListener(queues = RabbitConst.QUEUE_NAME_PROJECT)
public class ProjectConsumer {

	private final ProjectEventListenerPort projectEventListenerPort;

	@RabbitHandler
	@Transactional
	public void handleUserWithdrawEvent(UserWithdrawEvent event) {
		projectEventListenerPort.handleUserWithdrawEvent(event.getUserId());
	}
}
