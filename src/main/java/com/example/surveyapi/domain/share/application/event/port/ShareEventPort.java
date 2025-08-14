package com.example.surveyapi.domain.share.application.event.port;

import com.example.surveyapi.domain.share.application.event.dto.ShareCreateRequest;

public interface ShareEventPort {
	void handleSurveyEvent(ShareCreateRequest request);
}
