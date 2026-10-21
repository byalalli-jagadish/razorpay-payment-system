package com.razorpay.common.ratelimit;

public interface RateLimiter {
    RateLimitResult check(String key, int maxRequests, long windowSeconds);
}
