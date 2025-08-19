package com.example.surveyapi.domain.project.application;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.surveyapi.domain.project.application.event.ProjectEventPublisher;
import com.example.surveyapi.domain.project.domain.project.entity.Project;
import com.example.surveyapi.domain.project.domain.project.enums.ProjectState;
import com.example.surveyapi.domain.project.domain.project.repository.ProjectRepository;
import com.example.surveyapi.global.event.project.ProjectStateChangedEvent;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProjectStateScheduler {

	private final ProjectRepository projectRepository;
	private final ProjectEventPublisher projectEventPublisher;

	@Scheduled(cron = "0 0 0 * * *") // 매일 00시 실행
	@Transactional
	public void updateProjectStates() {
		LocalDateTime now = LocalDateTime.now();
		updatePendingProjects(now);
		updateInProgressProjects(now);
	}

	private void updatePendingProjects(LocalDateTime now) {
		List<Project> pendingProjects = projectRepository.findPendingProjectsToStart(now);
		if (pendingProjects.isEmpty()) {
			return;
		}

		List<Long> projectIds = pendingProjects.stream().map(Project::getId).toList();
		projectRepository.updateStateByIds(projectIds, ProjectState.IN_PROGRESS);

		pendingProjects.forEach(project ->
			projectEventPublisher.convertAndSend(
				new ProjectStateChangedEvent(project.getId(), ProjectState.IN_PROGRESS.name()))
		);
	}

	private void updateInProgressProjects(LocalDateTime now) {
		List<Project> inProgressProjects = projectRepository.findInProgressProjectsToClose(now);
		if (inProgressProjects.isEmpty()) {
			return;
		}

		List<Long> projectIds = inProgressProjects.stream().map(Project::getId).toList();
		projectRepository.updateStateByIds(projectIds, ProjectState.CLOSED);

		inProgressProjects.forEach(project ->
			projectEventPublisher.convertAndSend(
				new ProjectStateChangedEvent(project.getId(), ProjectState.CLOSED.name()))
		);
	}
}
