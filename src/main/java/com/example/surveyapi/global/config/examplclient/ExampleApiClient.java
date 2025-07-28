package com.example.surveyapi.global.config.examplclient;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange("/api/v1")
public interface ExampleApiClient {

	@GetExchange("/test/{id}")
	String getTestData(@PathVariable Long id, @RequestParam String name);

	// @PostExchange("/test")
	// String createTestData(@RequestBody Dto requestDto);
} 