package com.example.surveyapi.survey.application;

import com.example.surveyapi.config.TestMockConfig;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(properties = {
    "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration,org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration"
})
@ActiveProfiles("test")
@Transactional
@Import({TestMockConfig.class})
@Testcontainers
@TestPropertySource(properties = {
    "spring.main.allow-bean-definition-overriding=true",
    "spring.data.redis.host=localhost",
    "spring.data.redis.port=6379"
})
public abstract class IntegrationTestBase {

    @Container
    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:15")
        .withDatabaseName("testdb")
        .withUsername("test")
        .withPassword("test");

    @Container
    static final MongoDBContainer MONGODB = new MongoDBContainer("mongo:7")
        .withExposedPorts(27017);

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        // PostgreSQL 설정
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create");
        registry.add("spring.jpa.properties.hibernate.dialect", () -> "org.hibernate.dialect.PostgreSQLDialect");

        // MongoDB 설정
        registry.add("spring.data.mongodb.uri", MONGODB::getReplicaSetUrl);
        registry.add("spring.data.mongodb.database", () -> "test_survey_db");
    }
}