package com.example.surveyapi.global.config.client.user;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

import com.example.surveyapi.domain.user.application.dto.response.KakaoOauthResponse;

@HttpExchange(url = "https://kauth.kakao.com")
public interface KakaoApiClient {

    @PostExchange(
        url = "/oauth/token",
        contentType = "application/x-www-form-urlencoded;charset=utf-8")
    KakaoOauthResponse getKakaoAccessToken(
        @RequestParam("grant_type") String grant_type ,
        @RequestParam("client_id") String client_id,
        @RequestParam("redirect_uri") String redirect_uri,
        @RequestParam("code") String code
    );



}
