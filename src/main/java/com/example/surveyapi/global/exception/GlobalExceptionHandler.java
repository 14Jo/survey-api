package com.example.surveyapi.global.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.surveyapi.global.util.ApiResponse;

/**
 * 전역 예외처리 핸들러
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiResponse<Map<String, String>>> handleMethodArgumentNotValidException(
		MethodArgumentNotValidException e
	) {
		Map<String, String> errors = new HashMap<>();

		e.getBindingResult().getFieldErrors()
			.forEach((fieldError) -> {
				errors.put(fieldError.getField(), fieldError.getDefaultMessage());
			});
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
			.body(ApiResponse.error("Validation Error", errors));
	}

	@ExceptionHandler(CustomException.class)
	protected ApiResponse<Object> handleCustomException(CustomException e) {
		return ApiResponse.error(e.getMessage(), e.getErrorCode());
	}

}