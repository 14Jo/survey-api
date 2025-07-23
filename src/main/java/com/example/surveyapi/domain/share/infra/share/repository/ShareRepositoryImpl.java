package com.example.surveyapi.domain.share.infra.share.repository;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.example.surveyapi.domain.share.domain.share.entity.Share;
import com.example.surveyapi.domain.share.domain.share.repository.ShareRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ShareRepositoryImpl implements ShareRepository {
	private final ShareJpaRepository shareJpaRepository;

	@Override
	public Optional<Share> findBySurveyId(Long surveyId) {
		return shareJpaRepository.findBySurveyId(surveyId);
	}

	@Override
	public Optional<Share> findByLink(String link) {
		return shareJpaRepository.findByLink(link);
	}

	@Override
	public Share save(Share share) {
		return shareJpaRepository.save(share);
	}
}
