package com.r360.coupon_service.service.runtime;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;

// service/runtime/RateLimitService.java
@Component
public class RateLimitService {
    private final StringRedisTemplate redis;
    private final DefaultRedisScript<Long> script;
    public RateLimitService(StringRedisTemplate redis) {
        this.redis = redis;
        this.script = new DefaultRedisScript<>(
                """
                local k = KEYS[1]; local limit = tonumber(ARGV[1]); local ttl = tonumber(ARGV[2]);
                local v = redis.call('INCR', k)
                if v == 1 then redis.call('PEXPIRE', k, ttl) end
                if v > limit then return -1 else return v end
                """, Long.class);
    }
    public boolean withinLimit(String key, int limit, Duration window) {
        Long res = redis.execute(script, List.of(key), String.valueOf(limit), String.valueOf(window.toMillis()));
        return res > 0;
    }
}
