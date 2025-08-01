package com.example.surveyapi.domain.share.application.client;

import java.util.List;

public interface ShareServicePort {
	List<Long> getRecipientIds(Long shareId, Long requesterId);
}
