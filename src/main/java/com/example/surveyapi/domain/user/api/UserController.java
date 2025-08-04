package com.example.surveyapi.domain.user.api;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.surveyapi.domain.user.application.dto.request.UpdateUserRequest;
import com.example.surveyapi.domain.user.application.dto.response.UpdateUserResponse;
import com.example.surveyapi.domain.user.application.dto.response.UserGradeResponse;

import com.example.surveyapi.domain.user.application.dto.response.UserInfoResponse;
import com.example.surveyapi.domain.user.application.UserService;
import com.example.surveyapi.domain.user.application.dto.response.UserSnapShotResponse;
import com.example.surveyapi.global.util.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/v1/users")
    public ResponseEntity<ApiResponse<Page<UserInfoResponse>>> getUsers(
        Pageable pageable
    ) {
        Page<UserInfoResponse> all = userService.getAll(pageable);

        return ResponseEntity.status(HttpStatus.OK)
            .body(ApiResponse.success("회원 전체 조회 성공", all));
    }

    @GetMapping("/v1/users/me")
    public ResponseEntity<ApiResponse<UserInfoResponse>> getUser(
        @AuthenticationPrincipal Long userId
    ) {
        UserInfoResponse user = userService.getUser(userId);

        return ResponseEntity.status(HttpStatus.OK)
            .body(ApiResponse.success("회원 조회 성공", user));
    }

    @GetMapping("/v1/users/grade")
    public ResponseEntity<ApiResponse<UserGradeResponse>> getGrade(
        @AuthenticationPrincipal Long userId
    ) {
        UserGradeResponse success = userService.getGradeAndPoint(userId);

        return ResponseEntity.status(HttpStatus.OK)
            .body(ApiResponse.success("회원 등급 조회 성공", success));
    }

    @PatchMapping("/v1/users")
    public ResponseEntity<ApiResponse<UpdateUserResponse>> update(
        @Valid @RequestBody UpdateUserRequest request,
        @AuthenticationPrincipal Long userId
    ) {
        UpdateUserResponse update = userService.update(request, userId);

        return ResponseEntity.status(HttpStatus.OK)
            .body(ApiResponse.success("회원 정보 수정 성공", update));
    }

    @GetMapping("/v2/users/{userId}/snapshot")
    public ResponseEntity<ApiResponse<UserSnapShotResponse>> snapshot(
        @PathVariable Long userId
    ) {
        UserSnapShotResponse snapshot = userService.snapshot(userId);

        return ResponseEntity.status(HttpStatus.OK)
            .body(ApiResponse.success("스냅샷 정보", snapshot));
    }

}
