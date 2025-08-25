package com.example.surveyapi.share.domain.fcm.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

@Entity
@Table(name = "fcm_token")
@Getter
public class FcmToken {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Long userId;
	private String token;

	public FcmToken(Long userId, String token) {
		this.userId = userId;
		this.token = token;
	}

	public void updateToken(String newToken) {
		this.token = newToken;
	}
}
