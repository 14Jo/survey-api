package com.example.surveyapi.domain.share.application.event;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.example.surveyapi.domain.share.application.event.dto.ShareCreateRequest;
import com.example.surveyapi.domain.share.application.event.dto.ShareDeleteRequest;
import com.example.surveyapi.domain.share.application.event.port.ShareEventPort;
import com.example.surveyapi.global.event.RabbitConst;
import com.example.surveyapi.global.event.project.ProjectDeletedEvent;
import com.example.surveyapi.global.event.project.ProjectManagerAddedEvent;
import com.example.surveyapi.global.event.project.ProjectMemberAddedEvent;
import com.example.surveyapi.global.event.survey.SurveyActivateEvent;

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
	public void handleSurveyEvent(SurveyActivateEvent event) {
		try {
			log.info("Received survey event");

			ShareCreateRequest request = new ShareCreateRequest(
				event.getSurveyId(),
				event.getCreatorId(),
				event.getEndTime()
			);

			shareEventPort.handleSurveyEvent(request);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	@RabbitHandler
	public void handleProjectManagerEvent(ProjectManagerAddedEvent event) {
		try {
			log.info("Received project manager event");

			ShareCreateRequest request = new ShareCreateRequest(
				event.getProjectId(),
				event.getProjectOwnerId(),
				event.getPeriodEnd()
			);

			shareEventPort.handleProjectManagerEvent(request);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	@RabbitHandler
	public void handleProjectMemberEvent(ProjectMemberAddedEvent event) {
		try {
			log.info("Received project member event");

			ShareCreateRequest request = new ShareCreateRequest(
				event.getProjectId(),
				event.getProjectOwnerId(),
				event.getPeriodEnd()
			);

			shareEventPort.handleProjectMemberEvent(request);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	@RabbitHandler
	public void handleProjectDeleteEvent(ProjectDeletedEvent event) {
		try {
			log.info("Received project delete event");

			ShareDeleteRequest request = new ShareDeleteRequest(
				event.getProjectId(),
				event.getDeleterId()
			);

			shareEventPort.handleProjectDeleteEvent(request);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
}
