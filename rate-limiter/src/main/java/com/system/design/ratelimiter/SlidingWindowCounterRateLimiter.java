package com.system.design.ratelimiter;

import java.time.Duration;

/**
 * Sliding Window Counter Rate Limiter
 * ----------------------------------
 * Approximates sliding window using two counters:
 * current window and previous window.
 *
 * Pros:
 * - Low memory
 * - More accurate than fixed window
 *
 * Cons:
 * - Approximation (not exact)
 */

class SlidingWindowCounterRateLimiter implements RateLimiter {
    private final int limit;
    private final long windowMs;
    private long currentWindowStart;
    private int currentCount;
    private int previousCount;

    public SlidingWindowCounterRateLimiter(int limit, Duration window) {
        this.limit = limit;
        this.windowMs = window.toMillis();
        this.currentWindowStart = System.currentTimeMillis();
    }

    @Override
    public synchronized boolean allowRequest() {
        long now = System.currentTimeMillis();
        long elapsed = now - currentWindowStart;

        if (elapsed >= windowMs) {
            previousCount = currentCount;
            currentCount = 0;
            currentWindowStart = now;
            elapsed = 0;
        }

        double weight = 1.0 - ((double) elapsed / windowMs);
        double estimatedCount = previousCount * weight + currentCount;

        if (estimatedCount < limit) {
            currentCount++;
            return true;
        }
        return false;
    }
}

