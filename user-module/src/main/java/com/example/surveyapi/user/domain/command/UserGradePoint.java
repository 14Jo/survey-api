package com.example.surveyapi.user.domain.command;

import com.example.surveyapi.user.domain.user.enums.Grade;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserGradePoint {
    private Grade grade;
    private int point;
}
