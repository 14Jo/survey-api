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
	public ApiResponse<Object> handleMethodArgumentNotValidException(
		MethodArgumentNotValidException e
	) {
		BindingResult bindingResult = e.getBindingResult();
		String message = bindingResult.getFieldError().getDefaultMessage();
		return ApiResponse.error(message, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(CustomException.class)
	protected ApiResponse<Object> handleBusinessException(CustomException e) {
		return ApiResponse.error(e.getMessage(), HttpStatus.BAD_REQUEST);
	}

}