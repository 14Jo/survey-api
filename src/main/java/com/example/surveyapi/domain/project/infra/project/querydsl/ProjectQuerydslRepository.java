package com.example.surveyapi.domain.project.infra.project.querydsl;

import static com.example.surveyapi.domain.project.domain.participant.manager.entity.QProjectManager.*;
import static com.example.surveyapi.domain.project.domain.participant.member.entity.QProjectMember.*;
import static com.example.surveyapi.domain.project.domain.project.entity.QProject.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.example.surveyapi.domain.project.domain.dto.ProjectManagerResult;
import com.example.surveyapi.domain.project.domain.dto.ProjectMemberResult;
import com.example.surveyapi.domain.project.domain.dto.ProjectSearchResult;
import com.example.surveyapi.domain.project.domain.dto.QProjectManagerResult;
import com.example.surveyapi.domain.project.domain.dto.QProjectMemberResult;
import com.example.surveyapi.domain.project.domain.dto.QProjectSearchResult;
import com.example.surveyapi.domain.project.domain.participant.manager.entity.QProjectManager;
import com.example.surveyapi.domain.project.domain.participant.member.entity.QProjectMember;
import com.example.surveyapi.domain.project.domain.project.entity.Project;
import com.example.surveyapi.domain.project.domain.project.enums.ProjectState;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ProjectQuerydslRepository {

	private final JPAQueryFactory query;

	public List<ProjectManagerResult> findMyProjectsAsManager(Long currentUserId) {
		QProjectManager managerForCount = new QProjectManager("managerForCount");

		return query
			.select(new QProjectManagerResult(
				project.id,
				project.name,
				project.description,
				project.ownerId,
				projectManager.role.stringValue(),
				project.period.periodStart,
				project.period.periodEnd,
				project.state.stringValue(),
				managerForCount.id.count().intValue(),
				project.createdAt,
				project.updatedAt
			))
			.from(projectManager)
			.join(projectManager.project, project)
			.leftJoin(project.projectManagers, managerForCount).on(managerForCount.isDeleted.eq(false))
			.where(
				isManagerUser(currentUserId),
				isManagerNotDeleted(),
				isProjectNotDeleted()
			)
			.groupBy(
				project.id,
				project.name,
				project.description,
				project.ownerId,
				projectManager.role,
				project.period.periodStart,
				project.period.periodEnd,
				project.state,
				project.createdAt,
				project.updatedAt
			)
			.orderBy(project.createdAt.desc())
			.fetch();
	}

	public List<ProjectMemberResult> findMyProjectsAsMember(Long currentUserId) {
		QProjectMember memberForCount = new QProjectMember("memberForCount");

		return query
			.select(new QProjectMemberResult(
				project.id,
				project.name,
				project.description,
				project.ownerId,
				project.period.periodStart,
				project.period.periodEnd,
				project.state.stringValue(),
				memberForCount.id.count().intValue(),
				project.maxMembers,
				project.createdAt,
				project.updatedAt
			))
			.from(projectMember)
			.join(projectMember.project, project)
			.leftJoin(project.projectMembers, memberForCount).on(memberForCount.isDeleted.eq(false))
			.where(
				isMemberUser(currentUserId),
				isMemberNotDeleted(),
				isProjectNotDeleted()
			)
			.groupBy(
				project.id,
				project.name,
				project.description,
				project.ownerId,
				project.period.periodStart,
				project.period.periodEnd,
				project.state,
				project.maxMembers,
				project.createdAt,
				project.updatedAt
			)
			.orderBy(project.createdAt.desc())
			.fetch();
	}

	public Page<ProjectSearchResult> searchProjects(String keyword, Pageable pageable) {

		BooleanBuilder condition = createProjectSearchCondition(keyword);

		List<ProjectSearchResult> content = query
			.select(new QProjectSearchResult(
				project.id,
				project.name,
				project.description,
				project.ownerId,
				project.state.stringValue(),
				project.createdAt,
				project.updatedAt
			))
			.from(project)
			.where(condition)
			.orderBy(project.createdAt.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		Long total = query
			.select(project.count())
			.from(project)
			.where(condition)
			.fetchOne();

		return new PageImpl<>(content, pageable, total != null ? total : 0L);
	}

	public Optional<Project> findByIdAndIsDeletedFalse(Long projectId) {

		return Optional.ofNullable(
			query.selectFrom(project)
				.where(
					project.id.eq(projectId),
					isProjectActive()
				)
				.fetchFirst()
		);
	}

	public List<Project> findPendingProjectsToStart(LocalDateTime now) {

		return query.selectFrom(project)
			.where(
				project.state.eq(ProjectState.PENDING),
				isProjectActive(),
				project.period.periodStart.loe(now) // periodStart <= now
			)
			.fetch();
	}

	public List<Project> findInProgressProjectsToClose(LocalDateTime now) {

		return query.selectFrom(project)
			.where(
				project.state.eq(ProjectState.IN_PROGRESS),
				isProjectActive(),
				project.period.periodEnd.loe(now) // periodEnd <= now
			)
			.fetch();
	}

	public void updateStateByIds(List<Long> projectIds, ProjectState newState) {
		LocalDateTime now = LocalDateTime.now();

		query.update(project)
			.set(project.state, newState)
			.set(project.updatedAt, now)
			.where(project.id.in(projectIds))
			.execute();
	}

	public void removeMemberFromProjects(Long userId) {
		LocalDateTime now = LocalDateTime.now();

		query.update(projectMember)
			.set(projectMember.isDeleted, true)
			.set(projectMember.updatedAt, now)
			.where(projectMember.userId.eq(userId), projectMember.isDeleted.eq(false))
			.execute();
	}

	public void removeManagerFromProjects(Long userId) {
		LocalDateTime now = LocalDateTime.now();

		query.update(projectManager)
			.set(projectManager.isDeleted, true)
			.set(projectManager.updatedAt, now)
			.where(projectManager.userId.eq(userId), projectManager.isDeleted.eq(false))
			.execute();
	}

	// 내부 메소드

	private BooleanExpression isProjectActive() {

		return project.isDeleted.eq(false)
			.and(project.state.ne(ProjectState.CLOSED));
	}

	private BooleanExpression isProjectNotDeleted() {

		return project.isDeleted.eq(false);
	}

	private BooleanExpression isManagerNotDeleted() {

		return projectManager.isDeleted.eq(false);
	}

	private BooleanExpression isMemberNotDeleted() {

		return projectMember.isDeleted.eq(false);
	}

	private BooleanExpression isManagerUser(Long userId) {

		return userId != null ? projectManager.userId.eq(userId) : null;
	}

	private BooleanExpression isMemberUser(Long userId) {

		return userId != null ? projectMember.userId.eq(userId) : null;
	}

	// 키워드 조건 검색 생성
	private BooleanBuilder createProjectSearchCondition(String keyword) {
		BooleanBuilder builder = new BooleanBuilder();
		builder.and(isProjectNotDeleted());

		if (StringUtils.hasText(keyword)) {
			builder.and(
				project.name.containsIgnoreCase(keyword)
					.or(project.description.containsIgnoreCase(keyword))
			);
		}

		return builder;
	}
}
