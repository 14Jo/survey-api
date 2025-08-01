package com.example.surveyapi.global.config.client.user;

import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

import com.example.surveyapi.domain.user.application.client.KakaoAccessResponse;
import com.example.surveyapi.domain.user.application.client.KakaoUserInfoResponse;

@HttpExchange
public interface KakaoApiClient {

    @PostExchange(
        url = "https://kauth.kakao.com/oauth/token",
        contentType = "application/x-www-form-urlencoded;charset=utf-8")
    KakaoAccessResponse getKakaoAccessToken(
        @RequestParam("grant_type") String grant_type ,
        @RequestParam("client_id") String client_id,
        @RequestParam("redirect_uri") String redirect_uri,
        @RequestParam("code") String code
    );

    @GetExchange(url = "https://kapi.kakao.com/v2/user/me")
    KakaoUserInfoResponse getKakaoUserInfo(
        @RequestHeader("Authorization") String accessToken);



}
