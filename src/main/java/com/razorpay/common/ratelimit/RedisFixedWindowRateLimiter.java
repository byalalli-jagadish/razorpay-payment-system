package com.razorpay.common.ratelimit;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RedisFixedWindowRateLimiter implements RateLimiter {
    private final StringRedisTemplate redis;

    public RateLimitResult check(String key, int maxRequests, long windowSeconds) {
        String redisKey = "ratelimit:fixed:" + key;
        Long count = redis.opsForValue().increment(redisKey);
        if (count != null && count == 1L) {
            redis.expire(redisKey, Duration.ofSeconds(windowSeconds));
        }
        if (count == null || count <= maxRequests) {
            return new RateLimitResult(true, count == null ? maxRequests : (int) (maxRequests - count), 0);
        }
        Long ttl = redis.getExpire(redisKey, TimeUnit.SECONDS);
        return new RateLimitResult(false, 0, ttl == null || ttl < 1 ? 1 : ttl.intValue());
    }
}
