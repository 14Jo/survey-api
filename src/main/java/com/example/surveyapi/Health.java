package com.example.surveyapi;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;

@Entity
@Getter
public class Health {
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String status;

	public Health() {}
	public Health(String status) {
		this.status = status;
	}
}
