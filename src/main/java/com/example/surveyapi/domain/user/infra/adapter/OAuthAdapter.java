package com.example.surveyapi.domain.user.infra.adapter;

import org.springframework.stereotype.Component;

import com.example.surveyapi.domain.user.application.client.port.OAuthPort;
import com.example.surveyapi.domain.user.application.client.request.GoogleOAuthRequest;
import com.example.surveyapi.domain.user.application.client.request.KakaoOAuthRequest;
import com.example.surveyapi.domain.user.application.client.request.NaverOAuthRequest;
import com.example.surveyapi.domain.user.application.client.response.GoogleAccessResponse;
import com.example.surveyapi.domain.user.application.client.response.GoogleUserInfoResponse;
import com.example.surveyapi.domain.user.application.client.response.KakaoAccessResponse;
import com.example.surveyapi.domain.user.application.client.response.KakaoUserInfoResponse;
import com.example.surveyapi.domain.user.application.client.response.NaverAccessResponse;
import com.example.surveyapi.domain.user.application.client.response.NaverUserInfoResponse;
import com.example.surveyapi.global.client.OAuthApiClient;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OAuthAdapter implements OAuthPort {

    private final OAuthApiClient OAuthApiClient;

    @Override
    public KakaoAccessResponse getKakaoAccess(KakaoOAuthRequest request) {
        return OAuthApiClient.getKakaoAccessToken(
            request.getGrant_type(), request.getClient_id(),
            request.getRedirect_uri(), request.getCode());
    }

    @Override
    public KakaoUserInfoResponse getKakaoUserInfo(String accessToken) {
        return OAuthApiClient.getKakaoUserInfo(accessToken);
    }

    @Override
    public NaverAccessResponse getNaverAccess(NaverOAuthRequest request) {
        return OAuthApiClient.getNaverAccessToken(
            request.getGrant_type(), request.getClient_id(),
            request.getClient_secret(), request.getCode(),
            request.getState());
    }

    @Override
    public NaverUserInfoResponse getNaverUserInfo(String accessToken) {
        return OAuthApiClient.getNaverUserInfo(accessToken);
    }

    @Override
    public GoogleAccessResponse getGoogleAccess(GoogleOAuthRequest request) {
        return OAuthApiClient.getGoogleAccessToken(
            request.getGrant_type(), request.getClient_id(),
            request.getClient_secret(), request.getRedirect_uri(),
            request.getCode());
    }

    @Override
    public GoogleUserInfoResponse getGoogleUserInfo(String accessToken) {
        return OAuthApiClient.getGoogleUserInfo(accessToken);
    }
}
