package com.example.surveyapi.global.exception;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MissingRequestHeaderException;

import com.example.surveyapi.global.enums.CustomErrorCode;
import com.example.surveyapi.global.util.ApiResponse;

import io.jsonwebtoken.JwtException;
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

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ApiResponse<Void>> handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException e) {
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
            .body(ApiResponse.error("지원하지 않는 Content-Type 입니다."));
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<ApiResponse<Void>> handleMissingRequestHeaderException(MissingRequestHeaderException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.error("필수 헤더가 누락되었습니다."));
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ApiResponse<Void>> handleJwtException(JwtException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(ApiResponse.error("토큰이 유효하지 않습니다."));
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
        return ResponseEntity.status(CustomErrorCode.SERVER_ERROR.getHttpStatus())
            .body(ApiResponse.error("알 수 없는 오류"));
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

    // 필드 이름 추출 메서드
    private String resolveFieldName(MessageSourceResolvable error) {
        return (error instanceof FieldError fieldError) ? fieldError.getField() : "parameter";
    }
}