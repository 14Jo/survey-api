package com.example.surveyapi.domain.user.infra.adapter;

import java.util.Map;

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
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OAuthAdapter implements OAuthPort {

    private final OAuthApiClient OAuthApiClient;
    private final ObjectMapper objectMapper;

    @Override
    public KakaoAccessResponse getKakaoAccess(KakaoOAuthRequest request) {
        Map<String, Object> data = OAuthApiClient.getKakaoAccessToken(
            request.getGrant_type(), request.getClient_id(),
            request.getRedirect_uri(), request.getCode());

        return objectMapper.convertValue(data, KakaoAccessResponse.class);

    }

    @Override
    public KakaoUserInfoResponse getKakaoUserInfo(String accessToken) {
        Map<String, Object> data = OAuthApiClient.getKakaoUserInfo(accessToken);

        return objectMapper.convertValue(data, KakaoUserInfoResponse.class);
    }

    @Override
    public NaverAccessResponse getNaverAccess(NaverOAuthRequest request) {
        Map<String, Object> data = OAuthApiClient.getNaverAccessToken(
            request.getGrant_type(), request.getClient_id(),
            request.getClient_secret(), request.getCode(),
            request.getState());

        return objectMapper.convertValue(data, NaverAccessResponse.class);
    }

    @Override
    public NaverUserInfoResponse getNaverUserInfo(String accessToken) {
        Map<String, Object> data = OAuthApiClient.getNaverUserInfo(accessToken);

        return objectMapper.convertValue(data, NaverUserInfoResponse.class);
    }

    @Override
    public GoogleAccessResponse getGoogleAccess(GoogleOAuthRequest request) {
        Map<String, Object> data = OAuthApiClient.getGoogleAccessToken(
            request.getGrant_type(), request.getClient_id(),
            request.getClient_secret(), request.getRedirect_uri(),
            request.getCode());

        return objectMapper.convertValue(data, GoogleAccessResponse.class);
    }

    @Override
    public GoogleUserInfoResponse getGoogleUserInfo(String accessToken) {
        Map<String, Object> data = OAuthApiClient.getGoogleUserInfo(accessToken);

        return objectMapper.convertValue(data, GoogleUserInfoResponse.class);
    }
}
