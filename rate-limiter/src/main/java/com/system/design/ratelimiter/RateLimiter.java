package com.system.design.ratelimiter;

interface RateLimiter {
    boolean allowRequest();
}
