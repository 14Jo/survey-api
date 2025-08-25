package com.example.surveyapi.share.application.client;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserEmailDto {
	private Long userId;
	private String email;
}
