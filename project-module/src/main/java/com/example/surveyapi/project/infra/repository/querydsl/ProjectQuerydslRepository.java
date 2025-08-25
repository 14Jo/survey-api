package com.example.surveyapi.project.infra.repository.querydsl;

import static com.example.surveyapi.project.domain.participant.manager.entity.QProjectManager.*;
import static com.example.surveyapi.project.domain.participant.member.entity.QProjectMember.*;
import static com.example.surveyapi.project.domain.project.entity.QProject.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.example.surveyapi.project.domain.dto.ProjectManagerResult;
import com.example.surveyapi.project.domain.dto.ProjectMemberResult;
import com.example.surveyapi.project.domain.dto.ProjectSearchResult;
import com.example.surveyapi.project.domain.dto.QProjectManagerResult;
import com.example.surveyapi.project.domain.dto.QProjectMemberResult;
import com.example.surveyapi.project.domain.dto.QProjectSearchResult;
import com.example.surveyapi.project.domain.participant.manager.entity.QProjectManager;
import com.example.surveyapi.project.domain.participant.member.entity.QProjectMember;
import com.example.surveyapi.project.domain.project.entity.Project;
import com.example.surveyapi.project.domain.project.enums.ProjectState;
import com.example.surveyapi.global.util.RepositorySliceUtil;
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

	public Slice<ProjectSearchResult> searchProjectsNoOffset(String keyword, Long lastProjectId, Pageable pageable) {
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
			.where(condition, ltProjectId(lastProjectId))
			.orderBy(project.id.desc())
			.limit(pageable.getPageSize() + 1)
			.fetch();

		return RepositorySliceUtil.toSlice(content, pageable);
	}

	private BooleanExpression ltProjectId(Long lastProjectId) {
		if (lastProjectId == null) {
			return null;
		}
		return project.id.lt(lastProjectId);
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

	public List<Project> findAllWithParticipantsByUserId(Long userId) {

		// 카테시안 곱 발생
		// DDD 설계상 관점을 따라감
		return query.selectFrom(project)
			.distinct()
			.leftJoin(project.projectManagers, projectManager)
			.leftJoin(project.projectMembers, projectMember)
			.where(
				isProjectActive(),
				project.ownerId.eq(userId)
					.or(projectManager.userId.eq(userId).and(projectManager.isDeleted.eq(false)))
					.or(projectMember.userId.eq(userId).and(projectMember.isDeleted.eq(false)))
			)
			.fetch();
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
