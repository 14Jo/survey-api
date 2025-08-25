package com.example.surveyapi.share.application.fcm;

import org.springframework.stereotype.Service;

import com.example.surveyapi.share.domain.fcm.entity.FcmToken;
import com.example.surveyapi.share.domain.fcm.repository.FcmTokenRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FcmTokenService {
	private final FcmTokenRepository tokenRepository;

	public void saveToken(Long userId, String token) {
		tokenRepository.findByUserId(userId)
			.ifPresentOrElse(
				existing -> existing.updateToken(token),
				() -> tokenRepository.save(new FcmToken(userId, token))
			);
	}
}
