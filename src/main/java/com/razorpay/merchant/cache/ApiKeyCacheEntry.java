package com.razorpay.merchant.cache;

import java.util.UUID;
import java.time.LocalDateTime;

public record ApiKeyCacheEntry(String keyId, String secretHash, String previousSecretHash,
                               LocalDateTime graceExpiresAt, UUID merchantId, boolean enabled) {
    public boolean inGracePeriod() { return graceExpiresAt != null && LocalDateTime.now().isBefore(graceExpiresAt); }
}
