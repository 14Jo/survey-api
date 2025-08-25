package com.example.surveyapi.user.application.client.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GoogleOAuthRequest {
    private String grant_type;
    private String client_id;
    private String client_secret;
    private String redirect_uri;
    private String code;

    public static GoogleOAuthRequest of(
        String grant_type, String client_id,
        String client_secret, String redirect_uri, String code
    ) {
        GoogleOAuthRequest dto = new GoogleOAuthRequest();
        dto.grant_type = grant_type;
        dto.client_id = client_id;
        dto.client_secret = client_secret;
        dto.redirect_uri = redirect_uri;
        dto.code = code;

        return dto;
    }
}
