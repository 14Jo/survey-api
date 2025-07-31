package com.example.surveyapi.domain.participation.infra.adapter;

import org.springframework.stereotype.Component;

import com.example.surveyapi.domain.participation.application.client.UserServicePort;
import com.example.surveyapi.global.config.client.user.UserApiClient;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserServiceAdapter implements UserServicePort {

	private final UserApiClient userApiClient;

}
