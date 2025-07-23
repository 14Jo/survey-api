package com.example.surveyapi.domain.user.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.surveyapi.domain.user.application.dtos.request.LoginRequest;
import com.example.surveyapi.domain.user.application.dtos.request.SignupRequest;
import com.example.surveyapi.domain.user.application.dtos.response.LoginResponse;
import com.example.surveyapi.domain.user.application.dtos.response.SignupResponse;
import com.example.surveyapi.domain.user.application.service.UserService;
import com.example.surveyapi.global.util.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<SignupResponse>> signup(
        @RequestBody SignupRequest request) {

        SignupResponse signup = userService.signup(request);

        ApiResponse<SignupResponse> success = ApiResponse.success("회원가입 성공", signup);

        return ResponseEntity.status(HttpStatus.CREATED).body(success);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
        @RequestBody LoginRequest request) {

        LoginResponse login = userService.login(request);

        ApiResponse<LoginResponse> success = ApiResponse.success("로그인 성공", login);

        return ResponseEntity.status(HttpStatus.OK).body(success);
    }
}
