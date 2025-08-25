package com.example.surveyapi.domain.share.application.client;

public interface UserServicePort {

	UserEmailDto getUserByEmail(String authHeader, String email);
}
