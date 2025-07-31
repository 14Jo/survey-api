package com.example.surveyapi.domain.user.application.client;

import com.example.surveyapi.domain.user.application.dto.request.KakaoOauthRequest;
import com.example.surveyapi.domain.user.application.dto.response.KakaoOauthResponse;

public interface KakaoOauthPort {
    KakaoOauthResponse getKakaoOauthResponse(KakaoOauthRequest request);
}
