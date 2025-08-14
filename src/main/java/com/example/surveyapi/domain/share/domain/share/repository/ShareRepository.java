package com.example.surveyapi.domain.share.domain.share.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.surveyapi.domain.share.domain.share.entity.Share;
import com.example.surveyapi.domain.share.domain.share.vo.ShareSourceType;

public interface ShareRepository {
	Optional<Share> findByLink(String link);
	Share save(Share share);

	Optional<Share> findById(Long id);

	Optional<Share> findByToken(String token);

	void delete(Share share);

	Share findBySource(ShareSourceType sourceType, Long sourceId);

	List<Share> findBySourceId(Long sourceId);
}
