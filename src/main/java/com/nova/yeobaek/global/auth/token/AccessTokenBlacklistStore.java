package com.nova.yeobaek.global.auth.token;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class AccessTokenBlacklistStore {

    private static final String PREFIX = "BL:AT:";

    private final StringRedisTemplate redisTemplate;

    public void blacklist(String accessToken, long ttlMillis) {
        redisTemplate.opsForValue().set(
                PREFIX + accessToken,
                "logout",
                ttlMillis,
                TimeUnit.MILLISECONDS
        );
    }

    public boolean isBlacklisted(String accessToken) {
        return Boolean.TRUE.equals(
                redisTemplate.hasKey(PREFIX + accessToken)
        );
    }
}

