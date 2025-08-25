package com.example.surveyapi.share.domain.fcm.repository;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.example.surveyapi.share.domain.fcm.entity.FcmToken;

@Repository
public interface FcmTokenRepository {
	FcmToken save(FcmToken token);
	Optional<FcmToken> findByUserId(Long userId);
}
