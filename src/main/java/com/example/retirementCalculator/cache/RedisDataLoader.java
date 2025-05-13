package com.example.retirementCalculator.cache;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisDataLoader {

    private final StringRedisTemplate redisTemplate;

    @Autowired
    public RedisDataLoader(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @PostConstruct
    public void load() {
        redisTemplate.opsForValue().set("simple", "1000");
        redisTemplate.opsForValue().set("fancy", "3000");
    }
}