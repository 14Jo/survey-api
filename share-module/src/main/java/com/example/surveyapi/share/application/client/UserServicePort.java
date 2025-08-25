package com.example.surveyapi.share.application.client;

public interface UserServicePort {

	UserEmailDto getUserByEmail(String authHeader, String email);
}
