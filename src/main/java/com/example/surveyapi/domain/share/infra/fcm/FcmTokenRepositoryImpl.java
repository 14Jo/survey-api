package com.example.surveyapi.domain.share.infra.fcm;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.example.surveyapi.domain.share.domain.fcm.entity.FcmToken;
import com.example.surveyapi.domain.share.domain.fcm.repository.FcmTokenRepository;
import com.example.surveyapi.domain.share.infra.fcm.jpa.FcmTokenJpaRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class FcmTokenRepositoryImpl implements FcmTokenRepository {
	private final FcmTokenJpaRepository fcmTokenJpaRepository;

	@Override
	public FcmToken save(FcmToken token) {
		return fcmTokenJpaRepository.save(token);
	}
	@Override
	public Optional<FcmToken> findByUserId(Long userId) {
		return fcmTokenJpaRepository.findByUserId(userId);
	}
}
