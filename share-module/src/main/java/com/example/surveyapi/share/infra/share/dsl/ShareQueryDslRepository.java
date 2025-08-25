package com.example.surveyapi.share.infra.share.dsl;

public interface ShareQueryDslRepository {
	boolean isExist(Long surveyId, Long userId);
}
