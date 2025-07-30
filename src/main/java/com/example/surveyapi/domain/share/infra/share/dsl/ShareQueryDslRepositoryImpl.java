package com.example.surveyapi.domain.share.infra.share.dsl;

import org.springframework.stereotype.Repository;

import com.example.surveyapi.domain.share.domain.notification.entity.QNotification;
import com.example.surveyapi.domain.share.domain.share.entity.QShare;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ShareQueryDslRepositoryImpl implements ShareQueryDslRepository {
	private final JPAQueryFactory queryFactory;

	@Override
	public boolean isExist(Long surveyId, Long userId) {
		QShare share = QShare.share;
		QNotification notification = QNotification.notification;

		Integer fetchOne = queryFactory
			.selectOne()
			.from(share)
			.join(share.notifications, notification)
			.where(
				share.surveyId.eq(surveyId),
				notification.recipientId.eq(userId)
			)
			.fetchFirst();

		return fetchOne != null;
	}
}
