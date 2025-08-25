package com.example.surveyapi.user.infra.user.dsl;

import static com.example.surveyapi.user.domain.user.QUser.*;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.example.surveyapi.user.domain.user.User;
import com.example.surveyapi.global.exception.CustomErrorCode;
import com.example.surveyapi.global.exception.CustomException;

import com.querydsl.core.types.dsl.BooleanPath;
import com.querydsl.core.types.dsl.DateTimePath;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class QueryDslRepository {

    private final JPAQueryFactory queryFactory;
    private final BooleanPath isDeleted = Expressions.booleanPath(user,"isDeleted");
    private final DateTimePath<LocalDateTime> createdAt =
        Expressions.dateTimePath(LocalDateTime.class, user,"createdAt");

    public Page<User> gets(Pageable pageable) {

        Long total = queryFactory.
            select(user.count())
            .from(user)
            .where(isDeleted.eq(false))
            .fetchOne();

        long totalCount = total != null ? total : 0L;

        if (totalCount == 0L) {
            throw new CustomException(CustomErrorCode.USER_LIST_EMPTY);
        }

        List<User> users = queryFactory
            .selectFrom(user)
            .leftJoin(user.auth)
            .fetchJoin()
            .leftJoin(user.demographics)
            .fetchJoin()
            .where(isDeleted.eq(false))
            .orderBy(createdAt.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        return new PageImpl<>(users, pageable, totalCount);
    }
}
