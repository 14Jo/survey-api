package com.example.surveyapi.domain.user.application.dtos.response.select;

import com.example.surveyapi.domain.user.domain.user.User;
import com.example.surveyapi.domain.user.domain.user.enums.Grade;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GradeResponse {

    private final Grade grade;

    public static GradeResponse from(User user) {
        return new GradeResponse(user.getGrade());
    }
}
