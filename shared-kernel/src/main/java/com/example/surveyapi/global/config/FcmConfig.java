package com.example.surveyapi.global.config;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StringUtils;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class FcmConfig {

    // 기존 파일 경로 방식 (로컬 개발용)
    @Value("${firebase.credentials.path:}")
    private String firebaseCredentialsPath;

    // 환경변수 방식 (프로덕션용)
    @Value("${firebase.project-id:survey-f5a93}")
    private String projectId;

    @Value("${firebase.private-key-id:}")
    private String privateKeyId;

    @Value("${firebase.private-key:}")
    private String privateKey;

    @Value("${firebase.client-email:firebase-adminsdk-fbsvc@survey-f5a93.iam.gserviceaccount.com}")
    private String clientEmail;

    @Value("${firebase.client-id:100191250643521230154}")
    private String clientId;

    @Value("${firebase.enabled:true}")
    private boolean firebaseEnabled;

    @PostConstruct
    public void init() {
        if (!firebaseEnabled) {
            log.info("Firebase is disabled by configuration");
            return;
        }

        if (StringUtils.hasText(firebaseCredentialsPath)) {
            log.info("Firebase will be initialized using file: {}", firebaseCredentialsPath);
        } else if (StringUtils.hasText(privateKey) && StringUtils.hasText(privateKeyId)) {
            log.info("Firebase will be initialized using environment variables");
        } else {
            log.warn("Firebase credentials not found. Firebase features will be disabled.");
        }
    }

    @Bean
    public FirebaseApp firebaseApp() throws IOException {
        if (!firebaseEnabled) {
            log.warn("Firebase is disabled. Skipping FirebaseApp initialization.");
            return null;
        }

        InputStream credentialsStream = getCredentialsStream();

        if (credentialsStream == null) {
            log.error("Failed to get Firebase credentials. Firebase features will be disabled.");
            return null;
        }

        try {
            FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(credentialsStream))
                .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp app = FirebaseApp.initializeApp(options);
                log.info("FirebaseApp initialized successfully");
                return app;
            }
            return FirebaseApp.getInstance();
        } finally {
            credentialsStream.close();
        }
    }

    @Bean
    public FirebaseMessaging firebaseMessaging(FirebaseApp firebaseApp) {
        if (firebaseApp == null) {
            log.warn("FirebaseApp is null. FirebaseMessaging will not be available.");
            return null;
        }
        return FirebaseMessaging.getInstance(firebaseApp);
    }

    private InputStream getCredentialsStream() throws IOException {
        // 1. 먼저 파일 경로 방식 시도 (기존 방식 - 로컬 개발용)
        if (StringUtils.hasText(firebaseCredentialsPath)) {
            try {
                if (firebaseCredentialsPath.startsWith("classpath:")) {
                    ClassPathResource resource = new ClassPathResource(
                        firebaseCredentialsPath.replace("classpath:", "")
                    );
                    return resource.getInputStream();
                } else {
                    return new FileInputStream(firebaseCredentialsPath);
                }
            } catch (IOException e) {
                log.warn("Failed to load Firebase credentials from file: {}", e.getMessage());
            }
        }

        // 2. 환경변수 방식 시도 (프로덕션용)
        if (StringUtils.hasText(privateKey) && StringUtils.hasText(privateKeyId)) {
            String firebaseConfig = buildFirebaseConfig();
            return new ByteArrayInputStream(firebaseConfig.getBytes(StandardCharsets.UTF_8));
        }

        // 3. 둘 다 실패한 경우
        log.error("No Firebase credentials found. Check firebase.credentials.path or firebase.private-key/firebase.private-key-id");
        return null;
    }

    private String buildFirebaseConfig() {
        // 환경변수의 \n을 실제 개행 문자로 변환
        String formattedPrivateKey = privateKey.replace("\\n", "\n");

        return String.format("""
            {
              "type": "service_account",
              "project_id": "%s",
              "private_key_id": "%s",
              "private_key": "%s",
              "client_email": "%s",
              "client_id": "%s",
              "auth_uri": "https://accounts.google.com/o/oauth2/auth",
              "token_uri": "https://oauth2.googleapis.com/token",
              "auth_provider_x509_cert_url": "https://www.googleapis.com/oauth2/v1/certs",
              "client_x509_cert_url": "https://www.googleapis.com/robot/v1/metadata/x509/%s",
              "universe_domain": "googleapis.com"
            }
            """,
            projectId,
            privateKeyId,
            formattedPrivateKey,
            clientEmail,
            clientId,
            clientEmail.replace("@", "%40")
        );
    }
}
