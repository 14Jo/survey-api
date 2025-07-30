package com.example.surveyapi.domain.project.infra.project.querydsl;

import static com.example.surveyapi.domain.project.domain.manager.entity.QManager.*;
import static com.example.surveyapi.domain.project.domain.project.entity.QProject.*;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.example.surveyapi.domain.project.domain.dto.ProjectResult;
import com.example.surveyapi.domain.project.domain.dto.QProjectResult;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ProjectQuerydslRepository {
	private final JPAQueryFactory query;

	public List<ProjectResult> findMyProjects(Long currentUserId) {

		return query.select(new QProjectResult(
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

