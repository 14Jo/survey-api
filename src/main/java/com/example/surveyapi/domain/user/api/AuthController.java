package com.example.surveyapi.domain.user.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.surveyapi.domain.user.application.AuthService;
import com.example.surveyapi.domain.user.application.dto.request.LoginRequest;
import com.example.surveyapi.domain.user.application.dto.request.SignupRequest;
import com.example.surveyapi.domain.user.application.dto.request.UserWithdrawRequest;
import com.example.surveyapi.domain.user.application.dto.response.LoginResponse;
import com.example.surveyapi.domain.user.application.dto.response.SignupResponse;
import com.example.surveyapi.global.dto.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {

	private final AuthService authService;

	@PostMapping("/signup")
	public ResponseEntity<ApiResponse<SignupResponse>> signup(
		@Valid @RequestBody SignupRequest request
	) {
		SignupResponse signup = authService.signup(request);

		return ResponseEntity.status(HttpStatus.CREATED)
			.body(ApiResponse.success("회원가입 성공", signup));
	}

	@PostMapping("/login")
	public ResponseEntity<ApiResponse<LoginResponse>> login(
		@Valid @RequestBody LoginRequest request
	) {
		LoginResponse login = authService.login(request);

		return ResponseEntity.status(HttpStatus.OK)
			.body(ApiResponse.success("로그인 성공", login));
	}

	@PostMapping("/withdraw")
	public ResponseEntity<ApiResponse<Void>> withdraw(
		@Valid @RequestBody UserWithdrawRequest request,
		@AuthenticationPrincipal Long userId,
		@RequestHeader("Authorization") String authHeader
	) {
		authService.withdraw(userId, request, authHeader);

		return ResponseEntity.status(HttpStatus.OK)
			.body(ApiResponse.success("회원 탈퇴가 완료되었습니다.", null));
	}

	@PostMapping("/logout")
	public ResponseEntity<ApiResponse<Void>> logout(
		@RequestHeader("Authorization") String authHeader,
		@AuthenticationPrincipal Long userId
	) {
		authService.logout(authHeader, userId);

		return ResponseEntity.status(HttpStatus.OK)
			.body(ApiResponse.success("로그아웃 되었습니다.", null));
	}

	@PostMapping("/reissue")
	public ResponseEntity<ApiResponse<LoginResponse>> reissue(
		@RequestHeader("Authorization") String accessToken,
		@RequestHeader("RefreshToken") String refreshToken // Bearer 까지 넣어서
	) {
		LoginResponse reissue = authService.reissue(accessToken, refreshToken);

		return ResponseEntity.status(HttpStatus.OK)
			.body(ApiResponse.success("토큰이 재발급되었습니다.", reissue));
	}
}
