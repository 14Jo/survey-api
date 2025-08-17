package com.example.surveyapi.domain.share.infra.notification.dsl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.example.surveyapi.domain.share.application.notification.dto.NotificationResponse;
import com.example.surveyapi.domain.share.domain.notification.entity.Notification;
import com.example.surveyapi.domain.share.domain.notification.entity.QNotification;
import com.example.surveyapi.domain.share.domain.notification.vo.Status;
import com.example.surveyapi.domain.share.domain.share.entity.QShare;
import com.example.surveyapi.domain.share.domain.share.entity.Share;
import com.example.surveyapi.global.enums.CustomErrorCode;
import com.example.surveyapi.global.exception.CustomException;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class NotificationQueryDslRepositoryImpl implements NotificationQueryDslRepository {
	private final JPAQueryFactory queryFactory;

	@Override
	public Page<NotificationResponse> findByShareId(Long shareId, Long requesterId, Pageable pageable) {
		QNotification notification = QNotification.notification;
		QShare share = QShare.share;

		Share foundShare = queryFactory
			.selectFrom(share)
			.where(share.id.eq(shareId))
			.fetchOne();

		if(foundShare == null) {
			throw new CustomException(CustomErrorCode.NOT_FOUND_SHARE);
		}

		if(!foundShare.getCreatorId().equals(requesterId)) {
			throw new CustomException(CustomErrorCode.ACCESS_DENIED_SHARE);
		}

		List<Notification> content = queryFactory
			.selectFrom(notification)
			.where(notification.share.id.eq(shareId))
			.orderBy(notification.sentAt.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		long total = queryFactory
			.select(notification.count())
			.from(notification)
			.where(notification.share.id.eq(shareId))
			.fetchOne();

		List<NotificationResponse> responses = content.stream()
			.map(NotificationResponse::from)
			.collect(Collectors.toList());

		Page<NotificationResponse> pageResult = new PageImpl<>(responses, pageable, Optional.ofNullable(total).orElse(0L));

		return pageResult;
	}

	@Override
	public boolean isRecipient(Long sourceId, Long recipientId) {
		QNotification notification = QNotification.notification;
		QShare share = QShare.share;

		Long count = queryFactory
			.select(notification.count())
			.from(notification)
			.join(notification.share, share)
			.where(
				share.sourceId.eq(sourceId),
				notification.recipientId.eq(recipientId)
			).fetchOne();

		return count != null && count > 0;
	}

	@Override
	public Page<NotificationResponse> findByUserId(Long userId, Pageable pageable) {
		QNotification notification = QNotification.notification;

		List<Notification> content = queryFactory
			.selectFrom(notification)
			.where(notification.recipientId.eq(userId), notification.status.eq(Status.SENT))
			.orderBy(notification.sentAt.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		long total = queryFactory
			.select(notification.count())
			.from(notification)
			.where(notification.recipientId.eq(userId), notification.status.eq(Status.SENT))
			.fetchOne();

		List<NotificationResponse> responses = content.stream()
			.map(NotificationResponse::from)
			.collect(Collectors.toList());

		Page<NotificationResponse> pageResult = new PageImpl<>(responses, pageable, Optional.ofNullable(total).orElse(0L));

		return pageResult;
	}
}
