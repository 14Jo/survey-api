package com.example.surveyapi.user.application.client.port;

import java.time.Duration;

public interface UserRedisPort {
    Boolean delete (Long userId);

    String getRedisKey(String key);

    void saveRedisKey(String key, String value, Duration expire);
}
