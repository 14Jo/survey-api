package com.example.surveyapi.domain.user.domain.user;

import java.time.Duration;

public interface UserRedisRepository {
    Boolean delete (Long userId);

    String getRedisKey(String key);

    void saveRedisKey(String key, String value, Duration expire);
}
