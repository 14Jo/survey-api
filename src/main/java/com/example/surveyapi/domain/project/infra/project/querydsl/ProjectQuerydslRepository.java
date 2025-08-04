package com.example.surveyapi.domain.project.infra.project.querydsl;

import static com.example.surveyapi.domain.project.domain.manager.entity.QProjectManager.*;
import static com.example.surveyapi.domain.project.domain.member.entity.QProjectMember.*;
import static com.example.surveyapi.domain.project.domain.project.entity.QProject.*;

import java.util.List;

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
import com.example.surveyapi.domain.project.domain.project.entity.Project;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
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
				getManagerCountExpression(),
				project.createdAt,
				project.updatedAt
			))
			.from(projectManager)
			.join(projectManager.project, project)
			.where(
				isManagerUser(currentUserId),
				isManagerNotDeleted(),
				isProjectNotDeleted()
			)
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
				getManagerCountExpression(),
				getMemberCountExpression(),
				project.maxMembers,
				project.createdAt,
				project.updatedAt
			))
			.from(projectMember)
			.join(projectMember.project, project)
			.where(
				isMemberUser(currentUserId),
				isMemberNotDeleted(),
				isProjectNotDeleted()
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

	public List<Project> findProjectsByMember(Long userId) {

		return query.select(projectMember.project)
			.from(projectMember)
			.where(
				isMemberUser(userId),
				isMemberNotDeleted(),
				isProjectNotDeleted()
			)
			.fetch();
	}

	public List<Project> findProjectsByManager(Long userId) {

		return query.select(projectManager.project)
			.from(projectManager)
			.where(
				isManagerUser(userId),
				isManagerNotDeleted(),
				isProjectNotDeleted()
			)
			.fetch();
	}

	// 내부 메소드

	private BooleanExpression isProjectNotDeleted() {

		return project.isDeleted.eq(false);
	}

	private BooleanExpression isManagerNotDeleted() {

		return projectManager.isDeleted.eq(false);
	}

	private BooleanExpression isMemberNotDeleted() {

		return projectMember.isDeleted.eq(false);
	}

	/**
	 * 특정 사용자가 매니저인 조건
	 */
	private BooleanExpression isManagerUser(Long userId) {

		return userId != null ? projectManager.userId.eq(userId) : null;
	}

	/**
	 * 특정 사용자가 멤버인 조건
	 */
	private BooleanExpression isMemberUser(Long userId) {

		return userId != null ? projectMember.userId.eq(userId) : null;
	}

	/**
	 * 키워드 검색 조건 생성
	 */
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

	/**
	 * 프로젝트 매니저 수 카운트 서브쿼리
	 */
	private JPQLQuery<Integer> getManagerCountExpression() {

		return JPAExpressions
			.select(projectManager.count().intValue())
			.from(projectManager)
			.where(
				projectManager.project.eq(project),
				isManagerNotDeleted()
			);
	}

	/**
	 * 프로젝트 멤버 수 카운트 서브쿼리
	 */
	private JPQLQuery<Integer> getMemberCountExpression() {

		return JPAExpressions
			.select(projectMember.count().intValue())
			.from(projectMember)
			.where(
				projectMember.project.eq(project),
				isMemberNotDeleted()
			);
	}
}