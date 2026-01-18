package com.system.design.ratelimiter;

/**
 * Token Bucket Rate Limiter
 * -------------------------
 * Tokens are added at a fixed rate and consumed per request.
 * Allows bursts while enforcing average rate.
 *
 * Pros:
 * - Best for APIs
 * - Allows controlled bursts
 * - Low memory
 *
 * Used by:
 * AWS, Stripe, GCP, API gateways
 */

class TokenBucketRateLimiter implements RateLimiter {
    private final int capacity;
    private final int refillPerSecond;
    private double tokens;
    private long lastRefill;

    public TokenBucketRateLimiter(int capacity, int refillPerSecond) {
        this.capacity = capacity;
        this.refillPerSecond = refillPerSecond;
        this.tokens = capacity;
        this.lastRefill = System.nanoTime();
    }

    @Override
    public synchronized boolean allowRequest() {
        refill();
        if (tokens >= 1) {
            tokens--;
            return true;
        }
        return false;
    }

    /**
     * Refill tokens based on elapsed time since last refill.
     */
    private void refill() {
        long now = System.nanoTime();
        double seconds = (now - lastRefill) / 1_000_000_000.0;
        double added = seconds * refillPerSecond;
        if (added > 0) {
            tokens = Math.min(capacity, tokens + added);
            lastRefill = now;
        }
    }
}