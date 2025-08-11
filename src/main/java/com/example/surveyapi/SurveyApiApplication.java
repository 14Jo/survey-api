package com.example.surveyapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableCaching
@EnableAsync
@EnableCaching
@SpringBootApplication
public class SurveyApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(SurveyApiApplication.class, args);
	}

}
