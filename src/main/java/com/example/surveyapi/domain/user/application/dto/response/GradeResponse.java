package com.example.surveyapi.domain.user.application.dto.response;

import com.example.surveyapi.domain.user.domain.user.User;
import com.example.surveyapi.domain.user.domain.user.enums.Grade;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GradeResponse {

    private Grade grade;

    public static GradeResponse from(
        User user
    ) {
        GradeResponse dto = new GradeResponse();

        dto.grade = user.getGrade();

        return dto;
    }
}
