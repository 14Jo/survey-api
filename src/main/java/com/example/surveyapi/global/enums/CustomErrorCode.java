package com.example.surveyapi.global.enums;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum CustomErrorCode {

    EMAIL_DUPLICATED(HttpStatus.CONFLICT,"사용중인 이메일입니다."),
    WRONG_PASSWORD(HttpStatus.UNAUTHORIZED, "비밀번호가 틀렸습니다"),
    GRADE_NOT_FOUND(HttpStatus.NOT_FOUND, "등급을 조회 할 수 없습니다"),
    EMAIL_NOT_FOUND(HttpStatus.NOT_FOUND, "이메일을 찾을 수 없습니다."),
    ROLE_INVALID_FORMAT(HttpStatus.BAD_REQUEST, "유효하지 않은 UserRole"),
    NOT_FOUND_TOKEN(HttpStatus.NOT_FOUND,"토큰이 유효하지 않습니다."),
    NOT_FOUND_SURVEY(HttpStatus.NOT_FOUND, "설문이 존재하지 않습니다"),
    STATUS_INVALID_FORMAT(HttpStatus.BAD_REQUEST, "잘못된 상태코드입니다."),
    INVALID_PERMISSION(HttpStatus.FORBIDDEN, "작성 권한이 없습니다"),
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    CONFLICT(HttpStatus.CONFLICT, "요청이 충돌합니다."),
    BLACKLISTED_TOKEN(HttpStatus.NOT_FOUND,"블랙리스트 토큰입니다."),
    INVALID_TOKEN_TYPE(HttpStatus.BAD_REQUEST,"토큰 타입이 잘못되었습니다."),
    ACCESS_TOKEN_NOT_EXPIRED(HttpStatus.BAD_REQUEST,"아직 액세스 토큰이 만료되지 않았습니다."),
    NOT_FOUND_REFRESH_TOKEN(HttpStatus.NOT_FOUND,"리프레쉬 토큰이 없습니다."),
    MISMATCH_REFRESH_TOKEN(HttpStatus.BAD_REQUEST,"리프레쉬 토큰 맞지 않습니다."),
    PROJECT_ROLE_OWNER(HttpStatus.CONFLICT,"소유한 프로젝트가 존재합니다"),
    SURVEY_IN_PROGRESS(HttpStatus.CONFLICT,"참여중인 설문이 존재합니다."),
    PROVIDER_ID_NOT_FOUNT(HttpStatus.NOT_FOUND,"해당 providerId로 가입된 사용자가 존재하지 않습니다"),
    OAUTH_ACCESS_TOKEN_FAILED(HttpStatus.BAD_REQUEST,"소셜 로그인 인증에 실패했습니다"),

    // 프로젝트 에러
    START_DATE_AFTER_END_DATE(HttpStatus.BAD_REQUEST, "시작일은 종료일보다 이후일 수 없습니다."),
    DUPLICATE_PROJECT_NAME(HttpStatus.BAD_REQUEST, "중복 프로젝트 이름입니다."),
    NOT_FOUND_PROJECT(HttpStatus.NOT_FOUND, "프로젝트가 존재하지 않습니다."),
    NOT_FOUND_MANAGER(HttpStatus.NOT_FOUND, "담당자가 존재하지 않습니다."),
    INVALID_PROJECT_STATE(HttpStatus.BAD_REQUEST, "종료된 프로젝트 입니다."),
    INVALID_STATE_TRANSITION(HttpStatus.BAD_REQUEST, "PENDING -> IN_PROGRESS -> CLOSED 순서로만 변경 가능합니다."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "접근이 거부되었습니다."),
    ALREADY_REGISTERED_MANAGER(HttpStatus.CONFLICT, "이미 등록된 담당자입니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "유저를 찾을 수 없습니다"),
    CANNOT_CHANGE_OWNER_ROLE(HttpStatus.BAD_REQUEST, "OWNER는(로) 변경할 수 없습니다"),
    CANNOT_DELETE_SELF_OWNER(HttpStatus.BAD_REQUEST, "OWNER 본인은 삭제할 수 없습니다."),
    ALREADY_REGISTERED_MEMBER(HttpStatus.CONFLICT, "이미 등록된 인원입니다."),
    PROJECT_MEMBER_LIMIT_EXCEEDED(HttpStatus.CONFLICT, "프로젝트 최대 인원수를 초과하였습니다."),
    NOT_FOUND_MEMBER(HttpStatus.NOT_FOUND, "프로젝트에 참여한 이용자가 아닙니다."),
    CANNOT_TRANSFER_TO_SELF(HttpStatus.BAD_REQUEST, "자기 자신에게 소유권 이전 불가합니다."),

    // 통계 에러
    STATISTICS_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 생성된 통계"),

    // 참여 에러
    NOT_FOUND_PARTICIPATION(HttpStatus.NOT_FOUND, "참여 응답이 존재하지 않습니다."),
    ACCESS_DENIED_PARTICIPATION_VIEW(HttpStatus.FORBIDDEN, "본인의 참여 기록만 조회할 수 있습니다."),
	SURVEY_ALREADY_PARTICIPATED(HttpStatus.CONFLICT, "이미 참여한 설문입니다."),

    // 서버 에러
    USER_LIST_EMPTY(HttpStatus.INTERNAL_SERVER_ERROR, "회원 목록이 비어 있습니다. 데이터 상태를 확인하세요."),
    SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버에 장애가 생겼습니다."),

    // 공유 에러
    NOT_FOUND_SHARE(HttpStatus.NOT_FOUND, "공유 작업이 존재하지 않습니다."),
    ACCESS_DENIED_SHARE(HttpStatus.FORBIDDEN, "본인의 공유 작업 내역만 조회할 수 있습니다."),
    UNSUPPORTED_SHARE_METHOD(HttpStatus.BAD_REQUEST, "지원하지 않는 공유 방법 입니다.");

    private final HttpStatus httpStatus;
    private final String message;

    CustomErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

}