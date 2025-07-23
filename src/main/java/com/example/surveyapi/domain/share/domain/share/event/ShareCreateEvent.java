package com.example.surveyapi.domain.share.domain.share.event;

import lombok.Getter;

@Getter
public class ShareCreateEvent {
	private final Long shareId;
	private final Long surveyId;

	public ShareCreateEvent(Long shareId, Long surveyId) {
		this.shareId = shareId;
		this.surveyId = surveyId;
	}
}
