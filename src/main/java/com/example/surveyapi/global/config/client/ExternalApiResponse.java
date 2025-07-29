package com.example.surveyapi.global.config.client;

import java.time.LocalDateTime;

import com.example.surveyapi.global.enums.CustomErrorCode;
import com.example.surveyapi.global.exception.CustomException;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
public class ExternalApiResponse {
	private boolean success;
	private String message;
	private Object data;
	private LocalDateTime timestamp;

	private void throwIfFailed() {
		if (!success) {
			//TODO : 로깅 고도화
			log.warn("External API 호출 실패 - message: {}, timestamp: {}", message, timestamp);
			throw new CustomException(CustomErrorCode.SERVER_ERROR, message);
		}
	}

	public Object getOrThrow() {
		throwIfFailed();
		return data;
	}
}