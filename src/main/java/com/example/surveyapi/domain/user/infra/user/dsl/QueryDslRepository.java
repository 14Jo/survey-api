package com.example.surveyapi.domain.user.infra.user.dsl;

import static com.example.surveyapi.domain.user.domain.user.QUser.*;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;


import com.example.surveyapi.domain.user.application.dtos.response.UserResponse;
import com.example.surveyapi.domain.user.application.dtos.response.vo.AddressResponse;
import com.example.surveyapi.domain.user.application.dtos.response.vo.ProfileResponse;
import com.example.surveyapi.global.enums.CustomErrorCode;
import com.example.surveyapi.global.exception.CustomException;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class QueryDslRepository {

    private final JPAQueryFactory queryFactory;

    public Page<UserResponse> gets(Pageable pageable) {

        Long total = queryFactory.selectFrom(user).fetchCount();

        long totalCount = total != null ? total : 0L;

        if(totalCount == 0L) {
            throw new CustomException(CustomErrorCode.USER_LIST_EMPTY);
        }

        List<UserResponse> userList = queryFactory.select(Projections.constructor(
                UserResponse.class,
                user.id,
                user.auth.email,
                user.profile.name,
                user.role,
                user.grade,
                user.createdAt,
                Projections.constructor(
                    ProfileResponse.class,
                    user.profile.birthDate,
                    user.profile.gender,
                    Projections.constructor(
                        AddressResponse.class,
                        user.profile.address.province,
                        user.profile.address.district,
                        user.profile.address.detailAddress,
                        user.profile.address.postalCode
                    )
                )
            ))
            .from(user)
            .orderBy(user.createdAt.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        return new PageImpl<>(userList, pageable, totalCount);
    }
}
