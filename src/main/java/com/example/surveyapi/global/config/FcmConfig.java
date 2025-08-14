package com.example.surveyapi.global.config;

import java.io.FileInputStream;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;

@Configuration
public class FcmConfig {
	@Value("${firebase.credentials.path}")
	private String firebaseCredentialsPath;

	@Bean
	public FirebaseApp firebaseApp() throws IOException {
		ClassPathResource resource = new ClassPathResource(firebaseCredentialsPath.replace("classpath:", ""));
		FirebaseOptions options = FirebaseOptions.builder()
			.setCredentials(GoogleCredentials.fromStream(resource.getInputStream()))
			.build();

		if(FirebaseApp.getApps().isEmpty()) {
			return FirebaseApp.initializeApp(options);
		}
		return FirebaseApp.getInstance();
	}

	@Bean
	public FirebaseMessaging firebaseMessaging(FirebaseApp firebaseApp) {
		return FirebaseMessaging.getInstance(firebaseApp);
	}
}
