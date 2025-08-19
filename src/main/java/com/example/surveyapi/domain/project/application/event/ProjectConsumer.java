package com.example.surveyapi.domain.project.application.event;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.surveyapi.domain.project.domain.project.repository.ProjectRepository;
import com.example.surveyapi.global.constant.RabbitConst;
import com.example.surveyapi.global.event.UserWithdrawEvent;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@RabbitListener(
	queues = RabbitConst.QUEUE_NAME_PROJECT
)
public class ProjectConsumer {

	private final ProjectRepository projectRepository;

	@RabbitHandler
	@Transactional
	public void handleUserWithdrawEvent(UserWithdrawEvent event) {
		projectRepository.removeMemberFromProjects(event.getUserId());
		projectRepository.removeManagerFromProjects(event.getUserId());
		projectRepository.removeProjects(event.getUserId());
	}
}