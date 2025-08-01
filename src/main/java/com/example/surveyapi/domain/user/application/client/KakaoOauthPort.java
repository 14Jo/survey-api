package com.example.surveyapi.domain.user.application.client;

public interface KakaoOauthPort {
    KakaoAccessResponse getKakaoAccess(KakaoOauthRequest request);

    KakaoUserInfoResponse getKakaoUserInfo(String accessToken);

}
