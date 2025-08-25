package com.example.surveyapi;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@EnableCaching
@SpringBootApplication
@EnableRabbit
@EnableRetry
public class SurveyApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(SurveyApiApplication.class, args);
	}

}
