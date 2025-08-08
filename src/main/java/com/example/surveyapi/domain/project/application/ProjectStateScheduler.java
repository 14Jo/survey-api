package com.example.surveyapi.domain.project.application;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.surveyapi.domain.project.domain.project.entity.Project;
import com.example.surveyapi.domain.project.domain.project.enums.ProjectState;
import com.example.surveyapi.domain.project.domain.project.repository.ProjectRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProjectStateScheduler {

	private ProjectRepository projectRepository;

	@Scheduled(cron = "0 0 0 * * *") // 매일 00시 실행
	@Transactional
	public void updateProjectStates() {
		LocalDateTime now = LocalDateTime.now();
		updatePendingProjects(now);
		updateInProgressProjects(now);
	}

	private void updatePendingProjects(LocalDateTime now) {
		List<Project> pendingProjects = projectRepository.findPendingProjectsToStart(now);
		List<Long> projectIds = pendingProjects.stream().map(Project::getId).toList();
		projectRepository.updateStateByIds(projectIds, ProjectState.IN_PROGRESS);
	}

	private void updateInProgressProjects(LocalDateTime now) {
		List<Project> inProgressProjects = projectRepository.findInProgressProjectsToClose(now);
		List<Long> projectIds = inProgressProjects.stream().map(Project::getId).toList();
		projectRepository.updateStateByIds(projectIds, ProjectState.CLOSED);
	}
}
