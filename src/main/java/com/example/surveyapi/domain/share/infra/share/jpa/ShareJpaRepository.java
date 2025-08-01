package com.example.surveyapi.domain.share.infra.share.jpa;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.surveyapi.domain.share.domain.share.entity.Share;

public interface ShareJpaRepository extends JpaRepository<Share, Long> {
	Optional<Share> findBySurveyId(Long surveyId);

	Optional<Share> findByLink(String link);

	Optional<Share> findById(Long id);

	Optional<Share> findByToken(String token);
}
