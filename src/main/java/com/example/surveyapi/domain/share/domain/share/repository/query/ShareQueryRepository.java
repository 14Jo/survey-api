package com.example.surveyapi.domain.share.domain.share.repository.query;

public interface ShareQueryRepository {
	boolean isExist(Long surveyId, Long userId);
}
