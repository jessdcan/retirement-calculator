package com.example.retirementCalculator.cache;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Interface for the interest rate cache service.
 * <p>
 * Provides methods to interact with the Redis cache for interest rate data.
 * Implementations should handle cache initialization, retrieval, and management.
 * </p>
 */
public interface InterestRateCacheService {

    /**
     * Retrieves an interest rate from the cache by lifestyle type.
     *
     * @param lifestyleType The lifestyle type to look up (e.g., "simple", "fancy")
     * @return An Optional containing the interest rate if found, empty otherwise
     */
    Optional<BigDecimal> getInterestRateByLifestyleType(String lifestyleType);

    /**
     * Initializes the cache with data from the CSV file.
     * <p>
     * This method should be called during application startup to populate
     * the cache with interest rate data from the CSV file.
     * </p>
     */
    void initializeCache();

    /**
     * Refreshes the cache by reloading data from the CSV file.
     * <p>
     * This method should clear existing cache entries and reload the data.
     * </p>
     */
    void refreshCache();

    /**
     * Checks if the cache is healthy and contains data.
     *
     * @return true if the cache is healthy and contains data, false otherwise
     */
    boolean isCacheHealthy();
} 