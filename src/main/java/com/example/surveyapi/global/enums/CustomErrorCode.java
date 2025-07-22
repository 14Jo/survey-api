package com.example.surveyapi.global.enums;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum CustomErrorCode {

	ROLE_INVALID_FORMAT(HttpStatus.BAD_REQUEST, "유효하지 않은 UserRole"),
	NOT_FOUND_SURVEY(HttpStatus.NOT_FOUND, "설문이 존재하지 않습니다"),
	START_DATE_AFTER_END_DATE(HttpStatus.BAD_REQUEST, "시작일은 종료일보다 이후일 수 없습니다."),
	DUPLICATE_PROJECT_NAME(HttpStatus.BAD_REQUEST, "중복 프로젝트 이름입니다.");
  
	private final HttpStatus httpStatus;
	private final String message;

	CustomErrorCode(HttpStatus httpStatus, String message) {
		this.httpStatus = httpStatus;
		this.message = message;
	}

}