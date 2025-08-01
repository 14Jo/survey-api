package com.example.surveyapi.domain.share.infra.share;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.example.surveyapi.domain.share.domain.share.entity.Share;
import com.example.surveyapi.domain.share.domain.share.repository.ShareRepository;
import com.example.surveyapi.domain.share.infra.share.jpa.ShareJpaRepository;

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

	@Override
	public Optional<Share> findById(Long id) {
		return shareJpaRepository.findById(id);
	}
}
