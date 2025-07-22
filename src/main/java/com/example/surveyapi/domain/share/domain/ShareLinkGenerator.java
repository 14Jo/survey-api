package com.example.surveyapi.domain.share.domain;

import java.util.UUID;

import org.springframework.stereotype.Component;

@Component
public class ShareLinkGenerator {
	private static final String BASE_URL = "https://everysurvey.com/surveys/share/";

	public String generateLink(Long surveyId) {
		String token = UUID.randomUUID().toString().replace("-", "");
		return BASE_URL + token;
	}
}
