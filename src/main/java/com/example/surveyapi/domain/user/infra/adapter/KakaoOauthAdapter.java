package com.example.surveyapi.domain.user.infra.adapter;

import org.springframework.stereotype.Component;

import com.example.surveyapi.domain.user.application.client.KakaoOauthPort;
import com.example.surveyapi.domain.user.application.client.KakaoOauthRequest;
import com.example.surveyapi.domain.user.application.client.KakaoAccessResponse;
import com.example.surveyapi.domain.user.application.client.KakaoUserInfoResponse;
import com.example.surveyapi.global.config.client.user.KakaoApiClient;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class KakaoOauthAdapter implements KakaoOauthPort {

    private final KakaoApiClient kakaoApiClient;

    @Override
    public KakaoAccessResponse getKakaoAccess(KakaoOauthRequest request) {
        return kakaoApiClient.getKakaoAccessToken(
            request.getGrant_type(), request.getClient_id(),
            request.getRedirect_uri(), request.getCode());
    }

    @Override
    public KakaoUserInfoResponse getKakaoUserInfo(String accessToken) {
        return kakaoApiClient.getKakaoUserInfo(accessToken);
    }
}
