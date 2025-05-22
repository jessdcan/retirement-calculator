package com.example.retirementCalculator.cache;

import com.example.retirementCalculator.exception.CacheException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Implementation of the interest rate cache service using Redis.
 * <p>
 * Manages caching of interest rate data, including initialization, retrieval,
 * and refresh operations. Uses Redis as the caching mechanism.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class InterestRateCacheServiceImpl implements InterestRateCacheService {

    private static final Logger log = LoggerFactory.getLogger(InterestRateCacheServiceImpl.class);
    private static final String INTEREST_RATE_CACHE_KEY_PREFIX = "interest_rate:";
    private static final String INTEREST_RATE_ALL_CACHE_KEY = "interest_rate:all";
    private static final long CACHE_TTL_HOURS = 24;
    private static final String CSV_FILE_PATH = "lifestyleTypeInterestRate.csv";

    private final RedisTemplate<String, Object> redisTemplate;
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

    @Override
    public Optional<BigDecimal> getInterestRateByLifestyleType(String lifestyleType) {
        try {
            log.debug("Retrieving interest rate for lifestyle type: {}", lifestyleType);
            String cacheKey = INTEREST_RATE_CACHE_KEY_PREFIX + lifestyleType.toLowerCase();

            @SuppressWarnings("unchecked")
            BigDecimal cachedRate = (BigDecimal) valueOps.get(cacheKey);

            if (cachedRate != null) {
                log.debug("Cache hit for interest rate, found value: {}", cachedRate);
                return Optional.of(cachedRate);
            } else {
                log.debug("Cache miss for interest rate");
                return Optional.empty();
            }
        } catch (Exception e) {
            log.error("Error retrieving interest rate from cache: {}", e.getMessage(), e);
            throw new CacheException("Failed to retrieve interest rate from cache", e);
        }
    }

    @Override
    public void initializeCache() {
        try {
            log.info("Initializing interest rate cache from CSV file");

            List<InterestRateEntry> entries = parseCsvFile();
            if (entries.isEmpty()) {
                log.warn("No interest rate data found in CSV file for cache initialization");
                return;
            }

            log.debug("Loading {} interest rate records into cache", entries.size());

            // Cache each interest rate individually by lifestyle type
            for (InterestRateEntry entry : entries) {
                String cacheKey = INTEREST_RATE_CACHE_KEY_PREFIX + entry.lifestyleType().toLowerCase();
                valueOps.set(cacheKey, entry.interestRate(), CACHE_TTL_HOURS, TimeUnit.HOURS);
            }

            // Cache the complete list
            valueOps.set(INTEREST_RATE_ALL_CACHE_KEY, entries, CACHE_TTL_HOURS, TimeUnit.HOURS);

            log.info("Successfully initialized interest rate cache with {} records", entries.size());
        } catch (Exception e) {
            log.error("Failed to initialize interest rate cache: {}", e.getMessage(), e);
            throw new CacheException("Cache initialization failed", e);
        }
    }

    @Override
    public void refreshCache() {
        try {
            log.info("Refreshing interest rate cache from CSV file");

            // Clear all existing interest rate cache entries
            List<String> keysToDelete = new ArrayList<>();

            // Delete all interest rate entries
            redisTemplate.keys(INTEREST_RATE_CACHE_KEY_PREFIX + "*").forEach(keysToDelete::add);

            if (!keysToDelete.isEmpty()) {
                redisTemplate.delete(keysToDelete);
                log.debug("Deleted {} existing cache entries", keysToDelete.size());
            }

            // Reinitialize the cache
            initializeCache();

            log.info("Cache refresh completed successfully");
        } catch (Exception e) {
            log.error("Failed to refresh interest rate cache: {}", e.getMessage(), e);
            throw new CacheException("Cache refresh failed", e);
        }
    }

    @Override
    public boolean isCacheHealthy() {
        try {
            log.debug("Checking interest rate cache health");
            return redisTemplate.hasKey(INTEREST_RATE_ALL_CACHE_KEY);
        } catch (Exception e) {
            log.error("Cache health check failed: {}", e.getMessage(), e);
            return false;
        }
    }

    private List<InterestRateEntry> parseCsvFile() throws IOException {
        List<InterestRateEntry> entries = new ArrayList<>();
        ClassPathResource resource = new ClassPathResource(CSV_FILE_PATH);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            // Skip header line
            String line = reader.readLine();
            if (line == null || !line.equals("lifestyleType,interestRate")) {
                throw new IOException("Invalid CSV format: missing or incorrect header");
            }

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length != 2) {
                    log.warn("Skipping invalid line in CSV: {}", line);
                    continue;
                }

                try {
                    String lifestyleType = parts[0].trim();
                    BigDecimal interestRate = new BigDecimal(parts[1].trim());
                    entries.add(new InterestRateEntry(lifestyleType, interestRate));
                } catch (NumberFormatException e) {
                    log.warn("Skipping line with invalid interest rate: {}", line);
                }
            }
        }

        return entries;
    }

    private record InterestRateEntry(String lifestyleType, BigDecimal interestRate) {}
} 