package com.example.surveyapi.domain.user.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.surveyapi.domain.user.application.UserService;
import com.example.surveyapi.domain.user.application.dto.request.LoginRequest;
import com.example.surveyapi.domain.user.application.dto.request.SignupRequest;
import com.example.surveyapi.domain.user.application.dto.request.UserWithdrawRequest;
import com.example.surveyapi.domain.user.application.dto.response.LoginResponse;
import com.example.surveyapi.domain.user.application.dto.response.SignupResponse;
import com.example.surveyapi.global.util.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1")
public class AuthController {

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

    @PostMapping("/auth/withdraw")
    public ResponseEntity<ApiResponse<Void>> withdraw(
        @Valid @RequestBody UserWithdrawRequest request,
        @AuthenticationPrincipal Long userId,
        @RequestHeader("Authorization") String authHeader
    ) {
        userService.withdraw(userId, request, authHeader);

        return ResponseEntity.status(HttpStatus.OK)
            .body(ApiResponse.success("회원 탈퇴가 완료되었습니다.", null));
    }

    @PostMapping("/auth/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
        @RequestHeader("Authorization") String authHeader,
        @AuthenticationPrincipal Long userId
    ) {
        userService.logout(authHeader, userId);

        return ResponseEntity.status(HttpStatus.OK)
            .body(ApiResponse.success("로그아웃 되었습니다.", null));
    }

    @PostMapping("/auth/reissue")
    public ResponseEntity<ApiResponse<LoginResponse>> reissue(
        @RequestHeader("Authorization") String accessToken,
        @RequestHeader("RefreshToken") String refreshToken // Bearer 까지 넣어서
    ) {
        LoginResponse reissue = userService.reissue(accessToken, refreshToken);

        return ResponseEntity.status(HttpStatus.OK)
            .body(ApiResponse.success("토큰이 재발급되었습니다.", reissue));
    }
}
