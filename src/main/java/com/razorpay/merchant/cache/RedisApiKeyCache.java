package com.razorpay.merchant.cache;

import com.razorpay.merchant.entity.ApiKey;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RedisApiKeyCache implements ApiKeyCache {
    private static final String PREFIX = "apikey:";
    private static final Duration TTL = Duration.ofMinutes(5);
    private final StringRedisTemplate redis;

    public Optional<ApiKeyCacheEntry> get(String keyId) {
        try {
            var values = redis.opsForHash().entries(PREFIX + keyId);
            if (values.isEmpty()) return Optional.empty();
            return Optional.of(new ApiKeyCacheEntry(keyId,
                    (String) values.get("secretHash"),
                    (String) values.get("previousSecretHash"),
                    values.get("graceExpiresAt") == null ? null : java.time.LocalDateTime.parse((String) values.get("graceExpiresAt")),
                    UUID.fromString((String) values.get("merchantId")),
                    Boolean.parseBoolean((String) values.get("enabled"))));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public void put(ApiKeyCacheEntry entry) {
        String key = PREFIX + entry.keyId();
        redis.opsForHash().put(key, "secretHash", entry.secretHash());
        if (entry.previousSecretHash() != null) redis.opsForHash().put(key, "previousSecretHash", entry.previousSecretHash());
        if (entry.graceExpiresAt() != null) redis.opsForHash().put(key, "graceExpiresAt", entry.graceExpiresAt().toString());
        redis.opsForHash().put(key, "merchantId", entry.merchantId().toString());
        redis.opsForHash().put(key, "enabled", Boolean.toString(entry.enabled()));
        redis.expire(key, TTL);
    }

    public void evict(String keyId) { redis.delete(PREFIX + keyId); }
}
