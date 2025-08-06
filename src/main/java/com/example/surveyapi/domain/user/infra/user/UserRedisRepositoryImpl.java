package com.example.surveyapi.domain.user.infra.user;

import java.time.Duration;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import com.example.surveyapi.domain.user.domain.user.UserRedisRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class UserRedisRepositoryImpl implements UserRedisRepository {

    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public Boolean delete(Long userId) {
        String redisKey = "refreshToken" + userId;
        return redisTemplate.delete(redisKey);
    }

    @Override
    public String getRedisKey(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public void saveRedisKey(String key, String value, Duration expire) {
        redisTemplate.opsForValue().set(key, value, expire);
    }
}
