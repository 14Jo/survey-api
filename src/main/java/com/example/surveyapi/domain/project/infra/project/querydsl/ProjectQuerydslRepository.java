package com.example.surveyapi.domain.project.infra.project.querydsl;

import static com.example.surveyapi.domain.project.domain.manager.QManager.*;
import static com.example.surveyapi.domain.project.domain.project.QProject.*;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.example.surveyapi.domain.project.application.dto.response.QReadProjectResponse;
import com.example.surveyapi.domain.project.application.dto.response.ProjectResponse;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ProjectQuerydslRepository {
	private final JPAQueryFactory query;

	public List<ProjectResponse> findMyProjects(Long currentUserId) {

		return query.select(new QReadProjectResponse(
				project.id,
				project.name,
				project.description,
				project.ownerId,
				manager.role.stringValue(),
				project.period.periodStart,
				project.period.periodEnd,
				project.state.stringValue(),
				JPAExpressions
					.select(manager.count().intValue())
					.from(manager)
					.where(manager.project.eq(project).and(manager.isDeleted.eq(false))),
				project.createdAt,
				project.updatedAt
			))
			.from(manager)
			.join(manager.project, project)
			.where(manager.userId.eq(currentUserId).and(manager.isDeleted.eq(false)).and(project.isDeleted.eq(false)))
			.orderBy(project.createdAt.desc())
			.fetch();
	}
}

