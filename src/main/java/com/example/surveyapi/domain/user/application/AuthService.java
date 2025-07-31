package com.example.surveyapi.domain.user.application;

import org.springframework.stereotype.Service;

import com.example.surveyapi.domain.user.application.client.KakaoOauthPort;
import com.example.surveyapi.domain.user.application.dto.request.KakaoOauthRequest;
import com.example.surveyapi.domain.user.application.dto.response.KakaoOauthResponse;
import com.example.surveyapi.global.config.oauth.KakaoOauthProperties;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final KakaoOauthPort kakaoOauthPort;
    private final KakaoOauthProperties kakaoOauthProperties;

    public KakaoOauthResponse getKakaoAccessToken(String code){
        KakaoOauthRequest request = KakaoOauthRequest.of(
            "authorization_code",
            kakaoOauthProperties.getClientId(),
            kakaoOauthProperties.getRedirectUri(),
            code);

        return kakaoOauthPort.getKakaoOauthResponse(request);
    }
}
