package com.example.surveyapi.share.application.client;

public class ShareValidationResponse {
	private final boolean valid;

	public ShareValidationResponse(boolean valid) {
		this.valid = valid;
	}

	public boolean isValid() {
		return valid;
	}
}
