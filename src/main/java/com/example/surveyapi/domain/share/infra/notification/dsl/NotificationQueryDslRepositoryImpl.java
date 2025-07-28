package com.example.surveyapi.domain.share.infra.notification.dsl;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import com.example.surveyapi.domain.share.application.notification.dto.NotificationPageResponse;
import com.example.surveyapi.domain.share.domain.notification.entity.Notification;
import com.example.surveyapi.domain.share.domain.notification.entity.QNotification;
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
	public NotificationPageResponse findByShareId(Long shareId, Long requesterId, int page, int size) {
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

		Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "sentAt"));

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

		Page<Notification> pageResult = new PageImpl<>(content, pageable, Optional.ofNullable(total).orElse(0L));
		return NotificationPageResponse.from(pageResult);
	}
}
