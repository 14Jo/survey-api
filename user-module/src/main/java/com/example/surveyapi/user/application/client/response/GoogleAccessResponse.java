package com.example.surveyapi.user.application.client.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GoogleAccessResponse {

    @JsonProperty("access_token")
    private String access_token;

    @JsonProperty("expires_in")
    private Integer expires_in;

    @JsonProperty("refresh_token")
    private String refresh_token;

    @JsonProperty("scope")
    private String scope;

    @JsonProperty("token_type")
    private String token_type;
}
