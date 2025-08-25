package com.example.surveyapi.share.domain.share.repository.query;

public interface ShareQueryRepository {
	boolean isExist(Long surveyId, Long userId);
}
