package com.example.surveyapi.domain.share.domain.share.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.surveyapi.domain.share.domain.share.entity.Share;

public interface ShareRepository {
	Optional<Share> findBySurveyId(Long surveyId);
	Optional<Share> findByLink(String link);
	Share save(Share share);
}
