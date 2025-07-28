package com.example.surveyapi.domain.user.application.dto.response;

import com.example.surveyapi.domain.user.domain.user.enums.Grade;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserGradeResponse {

    private Grade grade;

    public static UserGradeResponse from(
        Grade grade
    ) {
        UserGradeResponse dto = new UserGradeResponse();

        dto.grade = grade;

        return dto;
    }
}
