package com.example.surveyapi.domain.project.infra.project.querydsl;

import static com.example.surveyapi.domain.project.domain.manager.entity.QProjectManager.*;
import static com.example.surveyapi.domain.project.domain.member.entity.QProjectMember.*;
import static com.example.surveyapi.domain.project.domain.project.entity.QProject.*;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.example.surveyapi.domain.project.domain.dto.ProjectManagerResult;
import com.example.surveyapi.domain.project.domain.dto.ProjectMemberResult;
import com.example.surveyapi.domain.project.domain.dto.QProjectManagerResult;
import com.example.surveyapi.domain.project.domain.dto.QProjectMemberResult;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ProjectQuerydslRepository {
	private final JPAQueryFactory query;

	public List<ProjectManagerResult> findMyProjectsAsManager(Long currentUserId) {

		return query.select(new QProjectManagerResult(
				project.id,
				project.name,
				project.description,
				project.ownerId,
				projectManager.role.stringValue(),
				project.period.periodStart,
				project.period.periodEnd,
				project.state.stringValue(),
				JPAExpressions
					.select(projectManager.count().intValue())
					.from(projectManager)
					.where(projectManager.project.eq(project).and(projectManager.isDeleted.eq(false))),
				project.createdAt,
				project.updatedAt
			))
			.from(projectManager)
			.join(projectManager.project, project)
			.where(projectManager.userId.eq(currentUserId)
				.and(projectManager.isDeleted.eq(false))
				.and(project.isDeleted.eq(false)))
			.orderBy(project.createdAt.desc())
			.fetch();
	}

	public List<ProjectMemberResult> findMyProjectsAsMember(Long currentUserId) {

		return query.select(new QProjectMemberResult(
				project.id,
				project.name,
				project.description,
				project.ownerId,
				project.period.periodStart,
				project.period.periodEnd,
				project.state.stringValue(),
				JPAExpressions
					.select(projectManager.count().intValue())
					.from(projectManager)
					.where(projectManager.project.eq(project).and(projectManager.isDeleted.eq(false))),
				JPAExpressions
					.select(projectMember.count().intValue())
					.from(projectMember)
					.where(projectMember.project.eq(project).and(projectMember.isDeleted.eq(false))),
				project.maxMembers,
				project.createdAt,
				project.updatedAt
			))
			.from(projectMember)
			.join(projectMember.project, project)
			.where(projectMember.userId.eq(currentUserId)
				.and(projectMember.isDeleted.eq(false))
				.and(project.isDeleted.eq(false)))
			.orderBy(project.createdAt.desc())
			.fetch();
	}
}

