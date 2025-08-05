package com.example.surveyapi.domain.user.domain.command;

import com.example.surveyapi.domain.user.domain.user.enums.Grade;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserGradePoint {
    private Grade grade;
    private int point;
}
