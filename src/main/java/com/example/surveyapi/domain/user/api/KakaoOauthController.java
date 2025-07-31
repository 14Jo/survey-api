package com.example.surveyapi.domain.user.api;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.surveyapi.domain.user.application.AuthService;
import com.example.surveyapi.domain.user.application.dto.response.KakaoOauthResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class KakaoOauthController {

    private final AuthService authService;

    @PostMapping("/auth/kakao/callback")
    public KakaoOauthResponse getKakaoAccessToken(@RequestParam String code) {
        return authService.getKakaoAccessToken(code);
    }

}
