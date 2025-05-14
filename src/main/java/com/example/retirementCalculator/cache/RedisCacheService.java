package com.example.retirementCalculator.cache;

import com.example.retirementCalculator.persistance.entities.LifestyleDepositsEntity;
import com.example.retirementCalculator.persistance.repositories.LifestyleDepositsRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class RedisCacheService {

    private static final Logger log = LoggerFactory.getLogger(RedisCacheService.class);


    private final RedisTemplate<String, Object> redisTemplate;
    private final LifestyleDepositsRepo lifestyleDepositsRepo;
    private static final String CACHE_KEY_PREFIX = "lifestyle:";

    @Autowired
    public RedisCacheService(RedisTemplate<String, Object> redisTemplate, LifestyleDepositsRepo lifestyleDepositsRepo) {
        this.redisTemplate = redisTemplate;
        this.lifestyleDepositsRepo = lifestyleDepositsRepo;
    }

    /**
     * Initialize cache on startup.
     */
    @PostConstruct
    public void preloadCache() {
        log.info("Preloading lifestyle data into Redis cache...");
        try {
            List<LifestyleDepositsEntity> lifestyles = lifestyleDepositsRepo.findAll();
            lifestyles.forEach(lifestyle -> {
                String key = CACHE_KEY_PREFIX + lifestyle.getLifestyleType().toLowerCase();
                redisTemplate.opsForValue().set(key, lifestyle, 30, TimeUnit.MINUTES);
                log.debug("Cached lifestyle: {}", key);
            });
            log.info("Lifestyle data successfully cached.");
        } catch (DataAccessException ex) {
            log.error("Failed to preload lifestyle data into Redis", ex);
        }
    }

    /**
     * Retrieve a lifestyle by type.
     * @param lifestyleType lifestyle type string (e.g. "simple", "fancy")
     * @return Optional containing the entity if found
     */
    public Optional<LifestyleDepositsEntity> getLifestyle(String lifestyleType) {
        String key = CACHE_KEY_PREFIX + lifestyleType.toLowerCase();
        try {
            Object cached = redisTemplate.opsForValue().get(key);
            if (cached instanceof LifestyleDepositsEntity) {
                log.info("Cache hit for lifestyle: {}", lifestyleType);
                return Optional.of((LifestyleDepositsEntity) cached);
            } else {
                log.info("Cache miss for lifestyle: {}. Fetching from DB...", lifestyleType);
                Optional<LifestyleDepositsEntity> entity = lifestyleDepositsRepo
                        .findAll()
                        .stream()
                        .filter(l -> l.getLifestyleType().equalsIgnoreCase(lifestyleType))
                        .findFirst();

                entity.ifPresent(l -> {
                    redisTemplate.opsForValue().set(key, l, 30, TimeUnit.MINUTES);
                    log.debug("Re-cached lifestyle: {}", key);
                });

                return entity;
            }
        } catch (Exception ex) {
            log.error("Error retrieving lifestyle data from cache for type: {}", lifestyleType, ex);
            return Optional.empty();
        }
    }

    /**
     * Clear and reload the entire cache manually.
     */
    public void refreshCache() {
        log.info("Refreshing lifestyle cache...");
        try {
            List<LifestyleDepositsEntity> lifestyles = lifestyleDepositsRepo.findAll();
            lifestyles.forEach(lifestyle -> {
                String key = CACHE_KEY_PREFIX + lifestyle.getLifestyleType().toLowerCase();
                redisTemplate.delete(key);
                redisTemplate.opsForValue().set(key, lifestyle, 30, TimeUnit.MINUTES);
                log.debug("Refreshed cache for lifestyle: {}", key);
            });
            log.info("Cache refresh complete.");
        } catch (DataAccessException ex) {
            log.error("Failed to refresh Redis cache", ex);
        }
    }
}

