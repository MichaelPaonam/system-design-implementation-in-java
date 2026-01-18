package com.system.design.ratelimiter;

import java.time.Duration;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Sliding Window Log Rate Limiter
 * -------------------------------
 * Stores timestamp of each request and counts only
 * those within the sliding time window.
 *
 * Pros:
 * - Very accurate
 *
 * Cons:
 * - High memory usage
 * - Slower for very large traffic
 */

class SlidingWindowRateLimiter implements RateLimiter {
    private final int limit;
    private final long windowMs;
    private final Deque<Long> log = new ArrayDeque<>();

    public SlidingWindowRateLimiter(int limit, Duration window) {
        this.limit = limit;
        this.windowMs = window.toMillis();
    }

    @Override
    public synchronized boolean allowRequest() {
        long now = System.currentTimeMillis();
        while (!log.isEmpty() && now - log.peekFirst() > windowMs) {
            log.pollFirst();
        }
        if (log.size() < limit) {
            log.addLast(now);
            return true;
        }
        return false;
    }
}
