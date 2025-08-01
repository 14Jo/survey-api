package com.example.surveyapi.domain.share.application.client;

public class ShareInfoDto {
	private final Long shareId;
	private final Long recipientId;

	public ShareInfoDto(Long shareId, Long recipientId) {
		this.shareId = shareId;
		this.recipientId = recipientId;
	}

	public Long getShareId() {
		return shareId;
	}

	public Long getRecipientId() {
		return recipientId;
	}
}
