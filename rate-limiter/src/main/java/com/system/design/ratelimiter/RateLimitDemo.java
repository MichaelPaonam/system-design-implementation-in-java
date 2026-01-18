package com.system.design.ratelimiter;

import java.time.*;

/*
 * Demo of 5 rate limiting algorithms:
 * 1. Fixed Window
 * 2. Sliding Window Log
 * 3. Sliding Window Counter
 * 4. Token Bucket
 * 5. Leaky Bucket
 *
 * The app returns current time in user's timezone if allowed,
 * otherwise prints "RATE LIMITED".
 */
public class RateLimitDemo {

    public static void main(String[] args) throws Exception {

        RateLimiter fixedWindow = new FixedWindowRateLimiter(5, Duration.ofSeconds(10));
        RateLimiter slidingLog = new SlidingWindowLogRateLimiter(5, Duration.ofSeconds(10));
        RateLimiter slidingCounter = new SlidingWindowCounterRateLimiter(5, Duration.ofSeconds(10));
        RateLimiter tokenBucket = new TokenBucketRateLimiter(5, 1);
        RateLimiter leakyBucket = new LeakyBucketRateLimiter(5, 1);

        ZoneId userZone = ZoneId.of("Asia/Kolkata");

        run("Fixed Window", fixedWindow, userZone);
        run("Sliding Window Log", slidingLog, userZone);
        run("Sliding Window Counter", slidingCounter, userZone);
        run("Token Bucket", tokenBucket, userZone);
        run("Leaky Bucket", leakyBucket, userZone);
    }

    private static void run(String name, RateLimiter limiter, ZoneId zone) throws Exception {
        System.out.println("\n--- " + name + " ---");
        for (int i = 1; i <= 10; i++) {
            if (limiter.allowRequest()) {
                System.out.println("Request " + i + " -> " + ZonedDateTime.now(zone));
            } else {
                System.out.println("Request " + i + " -> RATE LIMITED");
            }
            Thread.sleep(500);
        }
    }
}