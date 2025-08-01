package com.example.surveyapi.domain.share.domain.share.event;

import com.example.surveyapi.domain.share.domain.share.entity.Share;

import lombok.Getter;

@Getter
public class ShareCreateEvent {
	private final Share share;
	private final Long creatorId;

	public ShareCreateEvent(Share share, Long creatorId) {
		this.share = share;
		this.creatorId = creatorId;
	}
}
