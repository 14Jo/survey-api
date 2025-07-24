package com.example.surveyapi.global.exception;


import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import com.example.surveyapi.global.util.ApiResponse;

import lombok.extern.slf4j.Slf4j;

/**
 * 전역 예외처리 핸들러
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // @RequestBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleMethodArgumentNotValidException(
        MethodArgumentNotValidException e
    ) {
        log.warn("Validation failed : {}", e.getMessage());

        Map<String, String> errors = new HashMap<>();

        e.getBindingResult().getFieldErrors()
            .forEach((fieldError) -> {
                errors.put(fieldError.getField(), fieldError.getDefaultMessage());
            });

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.error("요청 데이터 검증에 실패하였습니다.", errors));
    }

    @ExceptionHandler(CustomException.class)
    protected ApiResponse<Object> handleCustomException(CustomException e) {

        log.warn("Custom exception occurred: [{}] {}", e.getErrorCode(), e.getMessage());

        return ApiResponse.error(e.getMessage(), e.getErrorCode());
    }

    // @PathVariable, @RequestParam
    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleMethodValidationException(
        HandlerMethodValidationException e
    ) {
        log.warn("Parameter validation failed: {}", e.getMessage());

        Map<String, String> errors = new HashMap<>();

        for (MessageSourceResolvable error : e.getAllErrors()) {
            String fieldName = resolveFieldName(error);
            String message = Objects.requireNonNullElse(error.getDefaultMessage(), "잘못된 요청입니다.");

            errors.merge(fieldName, message, (existing, newMsg) -> existing + ", " + newMsg);
        }

        if (errors.isEmpty()) {
            errors.put("parameter", "파라미터 검증에 실패했습니다");
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.error("요청 파라미터 검증에 실패하였습니다.", errors));
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleException(Exception e) {

        log.error("Unexpected error : {}", e.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiResponse.error("서버 내부 오류가 발생하였습니다.", "Internal Server Error"));
    }

    // 필드 이름 추출 메서드
    private String resolveFieldName(MessageSourceResolvable error) {
        return (error instanceof FieldError fieldError) ? fieldError.getField() : "parameter";
    }
}