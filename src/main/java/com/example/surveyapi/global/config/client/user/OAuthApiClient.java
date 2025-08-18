package com.example.surveyapi.global.config.client.user;

import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

import com.example.surveyapi.domain.user.application.client.response.GoogleAccessResponse;
import com.example.surveyapi.domain.user.application.client.response.GoogleUserInfoResponse;
import com.example.surveyapi.domain.user.application.client.response.KakaoAccessResponse;
import com.example.surveyapi.domain.user.application.client.response.KakaoUserInfoResponse;
import com.example.surveyapi.domain.user.application.client.response.NaverAccessResponse;
import com.example.surveyapi.domain.user.application.client.response.NaverUserInfoResponse;

@HttpExchange
public interface OAuthApiClient {

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


    @PostExchange(
        url = "https://nid.naver.com/oauth2.0/token",
        contentType = "application/x-www-form-urlencoded;charset=utf-8")
    NaverAccessResponse getNaverAccessToken(
        @RequestParam("grant_type") String grant_type,
        @RequestParam("client_id") String client_id,
        @RequestParam("client_secret") String client_secret,
        @RequestParam("code") String code,
        @RequestParam("state") String state
    );

    @GetExchange(url = "https://openapi.naver.com/v1/nid/me")
    NaverUserInfoResponse getNaverUserInfo(
        @RequestHeader("Authorization") String accessToken);


    @PostExchange(
        url = "https://oauth2.googleapis.com/token",
        contentType = "application/x-www-form-urlencoded;charset=utf-8")
    GoogleAccessResponse getGoogleAccessToken(
        @RequestParam("grant_type") String grant_type ,
        @RequestParam("client_id") String client_id,
        @RequestParam("client_secret")  String client_secret,
        @RequestParam("redirect_uri") String redirect_uri,
        @RequestParam("code") String code
    );

    @GetExchange(url = "https://openidconnect.googleapis.com/v1/userinfo")
    GoogleUserInfoResponse getGoogleUserInfo(
        @RequestHeader("Authorization") String accessToken);

}
