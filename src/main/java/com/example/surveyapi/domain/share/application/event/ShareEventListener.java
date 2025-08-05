package com.example.surveyapi.domain.share.application.event;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.example.surveyapi.domain.share.application.share.ShareService;import com.example.surveyapi.domain.share.domain.share.event.ShareCreateEvent;
import com.example.surveyapi.domain.share.domain.share.vo.ShareMethod;
import com.example.surveyapi.domain.share.domain.share.vo.ShareSourceType;
import com.example.surveyapi.global.event.SurveyActivateEvent;

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
			ShareMethod.URL,
			event.getEndTime(),
			recipientIds,
			LocalDateTime.now()
		);
	}

	@EventListener
	public void handleProjectManagerEvent() {
		log.info("프로젝트 매니저 공유 작업 생성 시작: {}", event.getProjectId());

		// TODO : Project Event 생성 후 작업
	}

	@EventListener
	public void handleProjectMemberEvent() {
		log.info("프로젝트 참여 인원 공유 작업 생성 시작: {}", event.getProjectId());

		// TODO : Project Event 생성 후 작업
	}
}
