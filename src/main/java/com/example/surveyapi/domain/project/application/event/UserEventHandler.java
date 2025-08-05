package com.example.surveyapi.domain.project.application.event;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.example.surveyapi.domain.project.domain.project.entity.Project;
import com.example.surveyapi.domain.project.domain.project.repository.ProjectRepository;
import com.example.surveyapi.global.event.UserWithdrawEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserEventHandler {

	private final ProjectRepository projectRepository;

	@TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
	public void handleUserWithdrawEvent(UserWithdrawEvent event) {
		log.debug("회원 탈퇴 이벤트 수신 userId: {}", event.getUserId());

		List<Project> projectsByMember = projectRepository.findProjectsByMember(event.getUserId());
		for (Project project : projectsByMember) {
			project.removeMember(event.getUserId());
		}

		List<Project> projectsByManager = projectRepository.findProjectsByManager(event.getUserId());
		for (Project project : projectsByManager) {
			project.removeManager(event.getUserId());
		}

		log.debug("회원 탈퇴 처리 완료 userId: {}", event.getUserId());
	}
}