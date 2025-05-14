package com.example.retirementCalculator.cache;

import com.example.retirementCalculator.api.controllers.RetirementCalculatorController;
import com.example.retirementCalculator.exception.CacheException;
import com.example.retirementCalculator.persistance.entities.LifestyleDepositsEntity;
import com.example.retirementCalculator.persistance.repositories.LifestyleDepositsRepo;
import com.example.retirementCalculator.cache.LifestyleCacheService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Implementation of the lifestyle cache service using Redis.
 * <p>
 * Manages caching of lifestyle deposit data, including initialization, retrieval,
 * and refresh operations. Uses Redis as the caching mechanism.
 * </p>
 *
 * @author Your Name
 * @version 1.0
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
public class LifestyleCacheServiceImpl implements LifestyleCacheService {

    private Logger log = org.slf4j.LoggerFactory.getLogger(RetirementCalculatorController.class);

    private static final String LIFESTYLE_CACHE_KEY_PREFIX = "lifestyle:";
    private static final String LIFESTYLE_ALL_CACHE_KEY = "lifestyle:all";
    private static final long CACHE_TTL_HOURS = 24;

    private final RedisTemplate<String, Object> redisTemplate;
    private final LifestyleDepositsRepo lifestyleRepository;

    private ValueOperations<String, Object> valueOps;

    /**
     * Initializes the Redis value operations interface.
     * Called after dependency injection is complete.
     */
    @PostConstruct
    public void init() {
        valueOps = redisTemplate.opsForValue();
        initializeCache();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<LifestyleDepositsEntity> getLifestyleByType(String lifestyleType) {
        try {
            log.debug("Retrieving lifestyle data from cache for type: {}", lifestyleType);
            String cacheKey = LIFESTYLE_CACHE_KEY_PREFIX + lifestyleType.toLowerCase();

            LifestyleDepositsEntity cachedLifestyle = (LifestyleDepositsEntity) valueOps.get(cacheKey);

            if (cachedLifestyle != null) {
                log.debug("Cache hit for lifestyle type: {}", lifestyleType);
                return Optional.of(cachedLifestyle);
            } else {
                log.debug("Cache miss for lifestyle type: {}, attempting to retrieve from database", lifestyleType);
                // Try to fetch from database and update cache if found
                return lifestyleRepository.findByLifestyleTypeIgnoreCase(lifestyleType)
                        .map(lifestyle -> {
                            valueOps.set(cacheKey, lifestyle, CACHE_TTL_HOURS, TimeUnit.HOURS);
                            log.debug("Added lifestyle to cache: {}", lifestyleType);
                            return lifestyle;
                        });
            }
        } catch (Exception e) {
            log.error("Error retrieving lifestyle from cache: {}", e.getMessage(), e);
            throw new CacheException("Failed to retrieve lifestyle data from cache", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<LifestyleDepositsEntity> getAllLifestyles() {
        try {
            log.debug("Retrieving all lifestyle data from cache");

            @SuppressWarnings("unchecked")
            List<LifestyleDepositsEntity> cachedLifestyles = (List<LifestyleDepositsEntity>) valueOps.get(LIFESTYLE_ALL_CACHE_KEY);

            if (cachedLifestyles != null && !cachedLifestyles.isEmpty()) {
                log.debug("Cache hit for all lifestyles, found {} items", cachedLifestyles.size());
                return cachedLifestyles;
            } else {
                log.debug("Cache miss for all lifestyles, retrieving from database");
                List<LifestyleDepositsEntity> lifestyles = lifestyleRepository.findAll();
                valueOps.set(LIFESTYLE_ALL_CACHE_KEY, lifestyles, CACHE_TTL_HOURS, TimeUnit.HOURS);
                log.debug("Added all lifestyles to cache: {} items", lifestyles.size());
                return lifestyles;
            }
        } catch (Exception e) {
            log.error("Error retrieving all lifestyles from cache: {}", e.getMessage(), e);
            throw new CacheException("Failed to retrieve all lifestyle data from cache", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void initializeCache() {
        try {
            log.info("Initializing lifestyle cache from database");

            List<LifestyleDepositsEntity> lifestyles = lifestyleRepository.findAll();
            if (lifestyles.isEmpty()) {
                log.warn("No lifestyle data found in database for cache initialization");
                return;
            }

            log.debug("Loading {} lifestyle records into cache", lifestyles.size());

            // Cache each lifestyle individually by type
            for (LifestyleDepositsEntity lifestyle : lifestyles) {
                String cacheKey = LIFESTYLE_CACHE_KEY_PREFIX + lifestyle.getLifestyleType().toLowerCase();
                valueOps.set(cacheKey, lifestyle, CACHE_TTL_HOURS, TimeUnit.HOURS);
            }

            // Cache the complete list
            valueOps.set(LIFESTYLE_ALL_CACHE_KEY, lifestyles, CACHE_TTL_HOURS, TimeUnit.HOURS);

            log.info("Successfully initialized lifestyle cache with {} records", lifestyles.size());
        } catch (Exception e) {
            log.error("Failed to initialize lifestyle cache: {}", e.getMessage(), e);
            throw new CacheException("Cache initialization failed", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void refreshCache() {
        try {
            log.info("Refreshing lifestyle cache from database");

            // Clear all existing lifestyle cache entries
            List<String> keysToDelete = new ArrayList<>();

            // Delete all lifestyle entries
            redisTemplate.keys(LIFESTYLE_CACHE_KEY_PREFIX + "*").forEach(keysToDelete::add);

            if (!keysToDelete.isEmpty()) {
                redisTemplate.delete(keysToDelete);
                log.debug("Deleted {} existing cache entries", keysToDelete.size());
            }

            // Reinitialize the cache
            initializeCache();

            log.info("Cache refresh completed successfully");
        } catch (Exception e) {
            log.error("Failed to refresh lifestyle cache: {}", e.getMessage(), e);
            throw new CacheException("Cache refresh failed", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isCacheHealthy() {
        try {
            log.debug("Checking cache health");
            return redisTemplate.hasKey(LIFESTYLE_ALL_CACHE_KEY);
        } catch (Exception e) {
            log.error("Cache health check failed: {}", e.getMessage(), e);
            return false;
        }
    }
}