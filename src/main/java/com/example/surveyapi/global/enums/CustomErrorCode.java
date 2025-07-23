package com.example.surveyapi.global.enums;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum CustomErrorCode {
	STATISTICS_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 생성된 통계"),
	ROLE_INVALID_FORMAT(HttpStatus.BAD_REQUEST, "유효하지 않은 UserRole"),
	;
	private final HttpStatus httpStatus;
	private final String message;

	CustomErrorCode(HttpStatus httpStatus, String message) {
		this.httpStatus = httpStatus;
		this.message = message;
	}

}