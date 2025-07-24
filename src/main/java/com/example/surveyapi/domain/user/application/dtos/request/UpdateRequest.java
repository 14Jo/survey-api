package com.example.surveyapi.domain.user.application.dtos.request;

import com.example.surveyapi.domain.user.application.dtos.request.vo.update.UpdateAuthRequest;
import com.example.surveyapi.domain.user.application.dtos.request.vo.update.UpdateProfileRequest;

import lombok.Getter;

@Getter
public class UpdateRequest {

    private UpdateAuthRequest auth;
    private UpdateProfileRequest profile;


}
