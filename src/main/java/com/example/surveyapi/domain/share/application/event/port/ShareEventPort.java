package com.example.surveyapi.domain.share.application.event.port;

import com.example.surveyapi.domain.share.application.event.dto.ShareCreateRequest;
import com.example.surveyapi.domain.share.application.event.dto.ShareDeleteRequest;

public interface ShareEventPort {
	void handleSurveyEvent(ShareCreateRequest request);

	void handleProjectCreateEvent(ShareCreateRequest request);

	void handleProjectDeleteEvent(ShareDeleteRequest request);
}
