package com.example.surveyapi.domain.share.infra.share;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.example.surveyapi.domain.share.domain.share.entity.Share;
import com.example.surveyapi.domain.share.domain.share.repository.ShareRepository;
import com.example.surveyapi.domain.share.domain.share.vo.ShareSourceType;
import com.example.surveyapi.domain.share.infra.share.jpa.ShareJpaRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ShareRepositoryImpl implements ShareRepository {
	private final ShareJpaRepository shareJpaRepository;

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

	@Override
	public Optional<Share> findByToken(String token) {
		return shareJpaRepository.findByToken(token);
	}

	@Override
	public void delete(Share share) {
		shareJpaRepository.delete(share);
	}

	@Override
	public List<Share> findBySource(Long sourceId) {
		return shareJpaRepository.findBySourceId(sourceId);
	}
}
