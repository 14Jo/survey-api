package com.example.surveyapi.domain.user.api.internal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.surveyapi.domain.user.application.dto.request.LoginRequest;
import com.example.surveyapi.domain.user.application.dto.request.SignupRequest;
import com.example.surveyapi.domain.user.application.dto.request.UpdateRequest;
import com.example.surveyapi.domain.user.application.dto.request.WithdrawRequest;
import com.example.surveyapi.domain.user.application.dto.response.GradeResponse;
import com.example.surveyapi.domain.user.application.dto.response.LoginResponse;
import com.example.surveyapi.domain.user.application.dto.response.SignupResponse;

import com.example.surveyapi.domain.user.application.dto.response.UserResponse;
import com.example.surveyapi.domain.user.application.UserService;
import com.example.surveyapi.global.util.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/auth/signup")
    public ResponseEntity<ApiResponse<SignupResponse>> signup(
        @Valid @RequestBody SignupRequest request
    ) {
        SignupResponse signup = userService.signup(request);

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("회원가입 성공", signup));
    }

    @PostMapping("/auth/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
        @Valid @RequestBody LoginRequest request
    ) {
        LoginResponse login = userService.login(request);

        return ResponseEntity.status(HttpStatus.OK)
            .body(ApiResponse.success("로그인 성공", login));
    }

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<Page<UserResponse>>> getUsers(
        Pageable pageable
    ) {
        Page<UserResponse> All = userService.getAll(pageable);

        return ResponseEntity.status(HttpStatus.OK)
            .body(ApiResponse.success("회원 전체 조회 성공", All));
    }

    @GetMapping("/users/me")
    public ResponseEntity<ApiResponse<UserResponse>> getUser(
        @AuthenticationPrincipal Long userId
    ) {
        UserResponse user = userService.getUser(userId);

        return ResponseEntity.status(HttpStatus.OK)
            .body(ApiResponse.success("회원 조회 성공", user));
    }

    @GetMapping("/users/grade")
    public ResponseEntity<ApiResponse<GradeResponse>> getGrade(
        @AuthenticationPrincipal Long userId
    ) {
        GradeResponse grade = userService.getGrade(userId);

        return ResponseEntity.status(HttpStatus.OK)
            .body(ApiResponse.success("회원 등급 조회 성공", grade));
    }

    @PatchMapping("/users")
    public ResponseEntity<ApiResponse<UserResponse>> update(
        @RequestBody UpdateRequest request,
        @AuthenticationPrincipal Long userId
    ) {
        UserResponse update = userService.update(request, userId);

        return ResponseEntity.status(HttpStatus.OK)
            .body(ApiResponse.success("회원 정보 수정 성공", update));
    }

    @PostMapping("/users/withdraw")
    public ResponseEntity<ApiResponse<Void>> withdraw(
        @Valid @RequestBody WithdrawRequest request,
        @AuthenticationPrincipal Long userId
    ){
        userService.withdraw(userId,request);

        return ResponseEntity.status(HttpStatus.OK)
            .body(ApiResponse.success("회원 탈퇴가 완료되었습니다.", null));
    }

}
