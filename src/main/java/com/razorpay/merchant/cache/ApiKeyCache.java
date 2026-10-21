package com.razorpay.merchant.cache;

import java.util.Optional;

public interface ApiKeyCache {
    Optional<ApiKeyCacheEntry> get(String keyId);
    void put(ApiKeyCacheEntry entry);
    void evict(String keyId);
}
