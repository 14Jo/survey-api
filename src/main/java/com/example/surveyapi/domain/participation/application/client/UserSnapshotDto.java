package com.example.surveyapi.domain.participation.application.client;

import com.example.surveyapi.domain.participation.domain.participation.enums.Gender;
import com.example.surveyapi.domain.participation.domain.participation.vo.Region;

import lombok.Getter;

@Getter
public class UserSnapshotDto {
	private String birth;
	private Gender gender;
	private Region region;
}
