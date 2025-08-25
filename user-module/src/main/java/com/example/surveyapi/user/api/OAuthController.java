package com.example.surveyapi.user.api;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.surveyapi.user.application.AuthService;
import com.example.surveyapi.user.application.dto.request.SignupRequest;
import com.example.surveyapi.user.application.dto.response.LoginResponse;
import com.example.surveyapi.global.dto.ApiResponse;


import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class OAuthController {

    private final AuthService authService;

    @PostMapping("/auth/kakao/login")
    public ResponseEntity<ApiResponse<LoginResponse>> kakaoLogin(
        @RequestParam("code") String code,
        @RequestBody SignupRequest request
    ) {
        LoginResponse login = authService.kakaoLogin(code, request);

        return ResponseEntity.status(HttpStatus.OK)
            .body(ApiResponse.success("로그인 성공", login));
    }


    @PostMapping("/auth/naver/login")
    public ResponseEntity<ApiResponse<LoginResponse>> naverLogin(
        @RequestParam("code") String code,
        @RequestBody SignupRequest request
    ){
        LoginResponse login = authService.naverLogin(code, request);

        return ResponseEntity.status(HttpStatus.OK)
            .body(ApiResponse.success("로그인 성공", login));
    }

    @PostMapping("/auth/google/login")
    public ResponseEntity<ApiResponse<LoginResponse>> googleLogin(
        @RequestParam("code") String code,
        @RequestBody SignupRequest request
    ){
        LoginResponse login = authService.googleLogin(code, request);

        return ResponseEntity.status(HttpStatus.OK)
            .body(ApiResponse.success("로그인 성공", login));
    }
}
