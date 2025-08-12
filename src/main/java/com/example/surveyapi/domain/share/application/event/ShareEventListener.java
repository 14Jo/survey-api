package com.example.surveyapi.domain.share.application.event;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.example.surveyapi.domain.share.application.share.ShareService;
import com.example.surveyapi.domain.share.domain.share.entity.Share;
import com.example.surveyapi.domain.share.domain.share.vo.ShareMethod;
import com.example.surveyapi.domain.share.domain.share.vo.ShareSourceType;
import com.example.surveyapi.global.event.SurveyActivateEvent;
import com.example.surveyapi.global.event.project.ProjectDeletedEvent;
import com.example.surveyapi.global.event.project.ProjectManagerAddedEvent;
import com.example.surveyapi.global.event.project.ProjectMemberAddedEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ShareEventListener {
	private final ShareService shareService;

	@EventListener
	public void handleSurveyActivateEvent(SurveyActivateEvent event) {
		log.info("설문 공유 작업 생성 시작: {}", event.getSurveyId());

		List<Long> recipientIds = Collections.emptyList();

		shareService.createShare(
			ShareSourceType.SURVEY,
			event.getSurveyId(),
			event.getCreatorID(),
			event.getEndTime(),
			recipientIds,
			LocalDateTime.now()
		);
	}

	@EventListener
	public void handleProjectManagerEvent(ProjectManagerAddedEvent event) {
		log.info("프로젝트 매니저 공유 작업 생성 시작: {}", event.getProjectId());

		List<Long> recipientIds = List.of(event.getUserId());

		shareService.createShare(
			ShareSourceType.PROJECT_MANAGER,
			event.getProjectId(),
			event.getProjectOwnerId(),
			event.getPeriodEnd(),
			recipientIds,
			LocalDateTime.now()
		);
	}

	@EventListener
	public void handleProjectMemberEvent(ProjectMemberAddedEvent event) {
		log.info("프로젝트 참여 인원 공유 작업 생성 시작: {}", event.getProjectId());

		List<Long> recipientIds = List.of(event.getUserId());

		shareService.createShare(
			ShareSourceType.PROJECT_MEMBER,
			event.getProjectId(),
			event.getProjectOwnerId(),
			event.getPeriodEnd(),
			recipientIds,
			LocalDateTime.now()
		);
	}

	@EventListener
	public void handleProjectDeleteEvent(ProjectDeletedEvent event) {
		log.info("프로젝트 삭제 시작: {}", event.getProjectId());

		List<Share> shares = shareService.getShareBySource(event.getProjectId());

		for (Share share: shares) {
			shareService.delete(share.getId(), event.getDeleterId());
		}
		log.info("프로젝트 삭제 완료");
	}
}
