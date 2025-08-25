package com.example.surveyapi.user.application.client.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NaverAccessResponse {
    @JsonProperty("access_token")
    private String access_token;

    @JsonProperty("refresh_token")
    private String refresh_token;

    @JsonProperty("token_type")
    private String token_type;

    @JsonProperty("expires_in")
    private Integer expires_in;

    @JsonProperty("error")
    private String error;

    @JsonProperty("error_description")
    private String error_description;
}
