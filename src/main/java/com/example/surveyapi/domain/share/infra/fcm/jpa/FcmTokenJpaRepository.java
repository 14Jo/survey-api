package com.example.surveyapi.domain.share.infra.fcm.jpa;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.surveyapi.domain.share.domain.fcm.entity.FcmToken;

public interface FcmTokenJpaRepository extends JpaRepository<FcmToken, Long> {
	Optional<FcmToken> findByUserId(Long userId);
}
