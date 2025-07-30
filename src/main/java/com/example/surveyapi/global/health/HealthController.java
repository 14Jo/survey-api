package com.example.surveyapi.global.health;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

	@PostMapping("/health/ok")
	public String isHealthy() {
		return "OK";
	}
}
