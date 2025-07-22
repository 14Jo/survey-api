package com.example.surveyapi.domain.project.application;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.example.surveyapi.domain.project.domain.dto.request.CreateProjectRequest;
import com.example.surveyapi.domain.project.domain.entity.Manager;
import com.example.surveyapi.domain.project.domain.entity.Project;
import com.example.surveyapi.domain.project.domain.vo.ProjectPeriod;
import com.example.surveyapi.domain.project.infra.ManagerRepository;
import com.example.surveyapi.domain.project.infra.ProjectRepository;
import com.example.surveyapi.global.enums.CustomErrorCode;
import com.example.surveyapi.global.exception.CustomException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

	private final ProjectRepository projectRepository;
	private final ManagerRepository managerRepository;

	@Override
	public Long create(CreateProjectRequest request, Long currentMemberId) {
		validateDuplicateName(request.getName());
		ProjectPeriod period = toPeriod(request.getPeriodStart(), request.getPeriodEnd());

		Project project = Project.create(
			request.getName(),
			request.getDescription(),
			currentMemberId,
			period
		);
		projectRepository.save(project);

		Manager manager = Manager.createOwner(project, currentMemberId);
		managerRepository.save(manager);

		// TODO: 이벤트 발행

		return project.getId();
	}

	private void validateDuplicateName(String name) {
		if (projectRepository.existsByName(name)) {
			throw new CustomException(CustomErrorCode.DUPLICATE_PROJECT_NAME);
		}
	}

	private ProjectPeriod toPeriod(LocalDateTime periodStart, LocalDateTime periodEnd) {

		if (periodEnd != null && periodStart.isAfter(periodEnd)) {
			throw new CustomException(CustomErrorCode.START_DATE_AFTER_END_DATE);
		}

		return new ProjectPeriod(periodStart, periodEnd);
	}
}
