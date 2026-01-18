package com.system.design.ratelimiter;

import java.time.Duration;


/**
 * Fixed Window Rate Limiter
 * -------------------------
 * Allows at most N requests in a fixed time window.
 *
 * Algorithm:
 * - Count requests in a window
 * - Reset counter when window expires
 *
 * Pros:
 * - Very simple
 * - Low memory
 *
 * Cons:
 * - Allows burst at window boundary
 * - Inaccurate for high-traffic APIs
 */

class FixedWindowRateLimiter implements RateLimiter {
    private final int limit;
    private final long windowMs;
    private long windowStart;
    private int count;

    public FixedWindowRateLimiter(int limit, Duration window) {
        this.limit = limit;
        this.windowMs = window.toMillis();
        this.windowStart = System.currentTimeMillis();
    }

    @Override
    public synchronized boolean allowRequest() {
        long now = System.currentTimeMillis();
        if (now - windowStart >= windowMs) {
            windowStart = now;
            count = 0;
        }
        count++;
        return count <= limit;
    }
}
