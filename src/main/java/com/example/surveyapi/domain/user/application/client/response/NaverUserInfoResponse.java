package com.example.surveyapi.domain.user.application.client.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NaverUserInfoResponse {

    @JsonProperty("response")
    private NaverUserInfo response;

    @Getter
    @NoArgsConstructor
    public static class NaverUserInfo{
        @JsonProperty("id")
        private String providerId;
    }


}
