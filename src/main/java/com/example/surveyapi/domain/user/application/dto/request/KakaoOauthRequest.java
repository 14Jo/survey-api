package com.example.surveyapi.domain.user.application.dto.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class KakaoOauthRequest {
    private String grant_type;
    private String client_id;
    private String redirect_uri;
    private String code;

    public static KakaoOauthRequest of(
        String grant_type, String client_id,
        String redirect_uri, String code
    ){
        KakaoOauthRequest dto = new KakaoOauthRequest();
        dto.grant_type = grant_type;
        dto.client_id = client_id;
        dto.redirect_uri = redirect_uri;
        dto.code = code;

        return dto;
    }
}
