package com.example.surveyapi.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class AsyncConfig {
    @Bean(name = "externalAPI")
    public TaskExecutor taskExecutor(){
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5); // 코어 스레드 개수
        executor.setMaxPoolSize(10); // 최대 스레드 개수
        executor.setQueueCapacity(100); // 작업 대기 큐 개수
        executor.setThreadNamePrefix("ExternalAPI-");
        executor.initialize();
        return executor;
    }
}
