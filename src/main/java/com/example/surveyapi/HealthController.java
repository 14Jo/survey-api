package com.example.surveyapi;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class HealthController {

	private final HealthRepository healthRepository;

	@PostMapping("/health/ok")
	public String isHealthy() {
		Health health = new Health("TestStatus");
		Health save = healthRepository.save(health);
		return save.getStatus();
	}
}
