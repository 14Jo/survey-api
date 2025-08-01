package com.example.surveyapi.domain.share.infra.share.dsl;

public interface ShareQueryDslRepository {
	boolean isExist(Long surveyId, Long userId);
}
