package com.example.surveyapi.domain.user.infra.user.dsl;

import static com.example.surveyapi.domain.user.domain.user.QUser.*;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.example.surveyapi.domain.user.domain.user.User;
import com.example.surveyapi.global.enums.CustomErrorCode;
import com.example.surveyapi.global.exception.CustomException;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class QueryDslRepository {

    private final JPAQueryFactory queryFactory;

    public Page<User> gets(Pageable pageable) {

        Long total = queryFactory.
            select(user.count())
            .from(user)
            .where(user.isDeleted.eq(false))
            .fetchOne();

        long totalCount = total != null ? total : 0L;

        if (totalCount == 0L) {
            throw new CustomException(CustomErrorCode.USER_LIST_EMPTY);
        }

        List<User> users = queryFactory
            .selectFrom(user)
            .where(user.isDeleted.eq(false))
            .orderBy(user.createdAt.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        return new PageImpl<>(users, pageable, totalCount);
    }
}
