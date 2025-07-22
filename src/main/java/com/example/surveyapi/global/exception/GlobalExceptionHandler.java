package com.example.surveyapi.global.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 전역 예외처리 핸들러
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, Object>> handleMethodArgumentNotValidException(
		MethodArgumentNotValidException e) {
		BindingResult bindingResult = e.getBindingResult();
		String message = bindingResult.getFieldError().getDefaultMessage();
		Map<String, Object> map = new HashMap<>();
		map.put("error", HttpStatus.BAD_REQUEST);
		map.put("message", message);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
	}

	@ExceptionHandler(CustomException.class)
	protected ResponseEntity<Map<String, Object>> handleBusinessException(CustomException e) {
		Map<String, Object> map = new HashMap<>();
		map.put("error", HttpStatus.BAD_REQUEST);
		map.put("message", e.getMessage());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
	}

}