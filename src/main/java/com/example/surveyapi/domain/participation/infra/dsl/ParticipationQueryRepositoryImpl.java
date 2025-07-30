package com.example.surveyapi.domain.participation.infra.dsl;

import static com.example.surveyapi.domain.participation.domain.participation.QParticipation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.example.surveyapi.domain.participation.domain.participation.query.ParticipationInfo;
import com.example.surveyapi.domain.participation.domain.participation.query.ParticipationQueryRepository;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ParticipationQueryRepositoryImpl implements ParticipationQueryRepository {

	private final JPAQueryFactory queryFactory;

	@Override
	public Page<ParticipationInfo> findParticipationsInfo(Long memberId, Pageable pageable) {
		List<ParticipationInfo> participations = queryFactory
			.select(Projections.constructor(
				ParticipationInfo.class,
				participation.id,
				participation.surveyId,
				participation.createdAt
			))
			.from(participation)
			.where(participation.memberId.eq(memberId))
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		Long total = queryFactory
			.select(participation.id.count())
			.from(participation)
			.where(participation.memberId.eq(memberId))
			.fetchOne();

		return new PageImpl<>(participations, pageable, total);
	}

	@Override
	public Map<Long, Long> countsBySurveyIds(List<Long> surveyIds) {
		Map<Long, Long> map = queryFactory
			.select(participation.surveyId, participation.id.count())
			.from(participation)
			.where(participation.surveyId.in(surveyIds))
			.groupBy(participation.surveyId)
			.fetch()
			.stream()
			.collect(Collectors.toMap(
				t -> t.get(participation.surveyId),
				t -> t.get(participation.id.count())));

		for (Long surveyId : surveyIds) {
			map.putIfAbsent(surveyId, 0L);
		}

		return map;
	}
}
