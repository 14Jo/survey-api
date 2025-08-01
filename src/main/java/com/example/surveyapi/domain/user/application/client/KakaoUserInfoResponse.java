package com.example.surveyapi.domain.user.application.client;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class KakaoUserInfoResponse {

    @JsonProperty("id")
    private Long providerId;
}
