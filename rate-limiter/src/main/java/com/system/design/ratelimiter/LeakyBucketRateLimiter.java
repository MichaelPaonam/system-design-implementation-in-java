package com.system.design.ratelimiter;

/**
 * Leaky Bucket Rate Limiter
 * -------------------------
 * Requests are added to a bucket and processed at a constant rate.
 * Excess requests are dropped when bucket is full.
 *
 * Pros:
 * - Smooth output rate
 * - Good for traffic shaping
 *
 * Cons:
 * - No bursts allowed
 */

class LeakyBucketRateLimiter implements RateLimiter {
    private final int capacity;
    private final int leakPerSecond;
    private double water;
    private long lastLeak;

    public LeakyBucketRateLimiter(int capacity, int leakPerSecond) {
        this.capacity = capacity;
        this.leakPerSecond = leakPerSecond;
        this.lastLeak = System.nanoTime();
    }

    @Override
    public synchronized boolean allowRequest() {
        leak();
        if (water < capacity) {
            water++;
            return true;
        }
        return false;
    }

    /**
     * Leak water from the bucket based on elapsed time.
     */
    private void leak() {
        long now = System.nanoTime();
        double seconds = (now - lastLeak) / 1_000_000_000.0;
        double leaked = seconds * leakPerSecond;
        if (leaked > 0) {
            water = Math.max(0, water - leaked);
            lastLeak = now;
        }
    }
}
