package com.example.surveyapi.domain.project.infra.project.querydsl;

import static com.example.surveyapi.domain.project.domain.manager.QManager.*;
import static com.example.surveyapi.domain.project.domain.project.QProject.*;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.example.surveyapi.domain.project.application.dto.response.QReadProjectResponse;
import com.example.surveyapi.domain.project.application.dto.response.ReadProjectResponse;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ProjectQuerydslRepository {
	private final JPAQueryFactory query;

	public Page<ReadProjectResponse> findMyProjects(Pageable pageable, Long currentUserId) {

		BooleanBuilder condition = isParticipatedBy(currentUserId);

		List<ReadProjectResponse> content = query
			.select(new QReadProjectResponse(
				project.id,
				project.name,
				project.description,
				project.ownerId,
				manager.role.stringValue(),
				project.period.periodStart,
				project.period.periodEnd,
				project.state.stringValue(),
				// 관리자 인원수 서브 쿼리
				JPAExpressions
					.select(manager.count().intValue())
					.from(manager)
					.where(manager.project.eq(project).and(manager.isDeleted.eq(false))),
				project.createdAt,
				project.updatedAt
			))
			.from(project)
			.leftJoin(project.managers, manager)
			.on(manager.userId.eq(currentUserId).and(manager.isDeleted.eq(false)))
			.where(condition)
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.orderBy(project.createdAt.desc())
			.fetch();

		return new PageImpl<>(content, pageable, content.size());
	}

	private BooleanBuilder isParticipatedBy(Long userId) {
		return new BooleanBuilder()
			.and(project.isDeleted.eq(false))
			.and(manager.userId.eq(userId).and(manager.isDeleted.eq(false)));
	}
}
