package com.example.surveyapi.user.application.client.request;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NaverOAuthRequest {
    private String grant_type;
    private String client_id;
    private String client_secret;
    private String code;
    private String state;

    public static NaverOAuthRequest of(
        String grant_type, String client_id,
        String client_secret, String code, String state
    ){
        NaverOAuthRequest dto = new NaverOAuthRequest();
        dto.grant_type = grant_type;
        dto.client_id = client_id;
        dto.client_secret = client_secret;
        dto.code = code;
        dto.state = state;

        return dto;
    }

}
