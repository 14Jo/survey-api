package com.example.surveyapi.domain.share.api.external;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.surveyapi.domain.share.application.fcm.FcmTokenService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/fcm")
public class FcmController {
	private final FcmTokenService tokenService;

	@PostMapping("/token")
	public ResponseEntity<Void> save(
		@RequestParam String token,
		@AuthenticationPrincipal Long userId
	) {
		tokenService.saveToken(userId, token);

		return ResponseEntity.status(HttpStatus.OK)
			.build();
	}
}
