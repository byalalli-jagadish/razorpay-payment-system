package com.razorpay.common.ratelimit;

public record RateLimitResult(boolean allowed, int remaining, int retryAfterSeconds) {}
