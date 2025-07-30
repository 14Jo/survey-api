package com.example.surveyapi.global.exception;

import com.example.surveyapi.global.enums.CustomErrorCode;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {

	private final CustomErrorCode errorCode;
	private final String customMessage;

	public CustomException(CustomErrorCode errorCode) {
		super(errorCode.getMessage());
		this.errorCode = errorCode;
		this.customMessage = errorCode.getMessage();
	}

	public CustomException(CustomErrorCode errorCode, String customMessage) {
		super(customMessage);  // enum message 대신 custom message 사용
		this.errorCode = errorCode;
		this.customMessage = customMessage;

	}

	@Override
	public String getMessage() {
		return customMessage;
	}

}