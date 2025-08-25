package com.example.surveyapi.user.application.client.port;

import com.example.surveyapi.user.application.client.request.GoogleOAuthRequest;
import com.example.surveyapi.user.application.client.request.NaverOAuthRequest;
import com.example.surveyapi.user.application.client.response.GoogleAccessResponse;
import com.example.surveyapi.user.application.client.response.GoogleUserInfoResponse;
import com.example.surveyapi.user.application.client.response.KakaoAccessResponse;
import com.example.surveyapi.user.application.client.request.KakaoOAuthRequest;
import com.example.surveyapi.user.application.client.response.KakaoUserInfoResponse;
import com.example.surveyapi.user.application.client.response.NaverAccessResponse;
import com.example.surveyapi.user.application.client.response.NaverUserInfoResponse;

public interface OAuthPort {
    KakaoAccessResponse getKakaoAccess(KakaoOAuthRequest request);

    KakaoUserInfoResponse getKakaoUserInfo(String accessToken);

    NaverAccessResponse getNaverAccess(NaverOAuthRequest request);

    NaverUserInfoResponse getNaverUserInfo(String accessToken);

    GoogleAccessResponse getGoogleAccess(GoogleOAuthRequest request);

    GoogleUserInfoResponse getGoogleUserInfo(String accessToken);
}
