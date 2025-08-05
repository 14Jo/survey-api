package com.example.surveyapi.domain.user.application.dto.response;

import com.example.surveyapi.domain.user.domain.command.UserGradePoint;
import com.example.surveyapi.domain.user.domain.user.enums.Grade;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserGradeResponse {

    private Grade grade;
    private int point;

    public static UserGradeResponse from(
        UserGradePoint userGradePoint
    ) {
        UserGradeResponse dto = new UserGradeResponse();

        dto.grade = userGradePoint.getGrade();
        dto.point = userGradePoint.getPoint();

        return dto;
    }
}
