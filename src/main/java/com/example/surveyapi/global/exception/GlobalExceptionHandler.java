package com.example.surveyapi.global.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.surveyapi.global.enums.CustomErrorCode;
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
	protected ResponseEntity<ApiResponse<Void>> handleCustomException(CustomException e) {
		return ResponseEntity.status(e.getErrorCode().getHttpStatus())
			.body(ApiResponse.error(e.getErrorCode().getMessage()));
	}

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ApiResponse<Void>> handleAccessDenied(AccessDeniedException e) {
		return ResponseEntity.status(CustomErrorCode.ACCESS_DENIED.getHttpStatus())
			.body(ApiResponse.error(CustomErrorCode.ACCESS_DENIED.getMessage()));
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ApiResponse<Void>> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
			.body(ApiResponse.error("요청 데이터의 타입이 올바르지 않습니다."));
	}

	@ExceptionHandler(Exception.class)
	protected ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
		return ResponseEntity.status(CustomErrorCode.SERVER_ERROR.getHttpStatus())
			.body(ApiResponse.error("알 수 없는 오류"));
	}
}