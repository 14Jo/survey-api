package com.example.surveyapi.domain.user.application.client.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class KakaoOAuthRequest {
    private String grant_type;
    private String client_id;
    private String redirect_uri;
    private String code;

    public static KakaoOAuthRequest of(
        String grant_type, String client_id,
        String redirect_uri, String code
    ){
        KakaoOAuthRequest dto = new KakaoOAuthRequest();
        dto.grant_type = grant_type;
        dto.client_id = client_id;
        dto.redirect_uri = redirect_uri;
        dto.code = code;

        return dto;
    }
}
