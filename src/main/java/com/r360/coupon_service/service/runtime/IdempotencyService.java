package com.r360.coupon_service.service.runtime;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;

@Component
public class IdempotencyService {
    private final StringRedisTemplate redis;
    public IdempotencyService(StringRedisTemplate redis) { this.redis = redis; }
    public boolean acquire(String key, Duration ttl, String payloadHash) {
        Boolean ok = redis.opsForValue().setIfAbsent("idem:"+key, payloadHash, ttl);
        return Boolean.TRUE.equals(ok);
    }
    public Optional<String> get(String key) {
        String v = redis.opsForValue().get("idem:"+key);
        return Optional.ofNullable(v);
    }
}