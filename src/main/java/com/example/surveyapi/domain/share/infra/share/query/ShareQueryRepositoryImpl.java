package com.example.surveyapi.domain.share.infra.share.query;

import org.springframework.stereotype.Repository;

import com.example.surveyapi.domain.share.domain.share.repository.query.ShareQueryRepository;
import com.example.surveyapi.domain.share.infra.share.dsl.ShareQueryDslRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ShareQueryRepositoryImpl implements ShareQueryRepository {
	private final ShareQueryDslRepository dslRepository;

	@Override
	public boolean isExist(Long surveyId, Long userId) {

		return dslRepository.isExist(surveyId, userId);
	}
}
