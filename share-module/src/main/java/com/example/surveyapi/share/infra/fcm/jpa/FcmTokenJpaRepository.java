package com.example.surveyapi.share.infra.fcm.jpa;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.surveyapi.share.domain.fcm.entity.FcmToken;

public interface FcmTokenJpaRepository extends JpaRepository<FcmToken, Long> {
	Optional<FcmToken> findByUserId(Long userId);
}
