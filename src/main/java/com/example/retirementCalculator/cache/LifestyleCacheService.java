package com.example.retirementCalculator.cache;

import com.example.retirementCalculator.persistance.entities.LifestyleDepositsEntity;

import java.util.List;
import java.util.Optional;

/**
 * Interface for the lifestyle data cache service.
 * <p>
 * Provides methods to interact with the Redis cache for lifestyle deposit data.
 * Implementations should handle cache initialization, retrieval, and management.
 * </p>
 *
 * @author Your Name
 * @version 1.0
 * @since 1.0
 */
public interface LifestyleCacheService {

    /**
     * Retrieves a lifestyle deposit entity from the cache by its type.
     * <p>
     * Looks up cached lifestyle data based on the provided lifestyle type.
     * If the data is not found in cache, implementations should determine
     * whether to fetch from the database or return empty.
     * </p>
     *
     * @param lifestyleType The lifestyle type to look up (e.g., "simple", "fancy")
     * @return An Optional containing the lifestyle deposit entity if found, empty otherwise
     */
    Optional<LifestyleDepositsEntity> getLifestyleByType(String lifestyleType);

    /**
     * Retrieves all lifestyle deposit entities from the cache.
     * <p>
     * Returns a complete list of all available lifestyle types and their data.
     * </p>
     *
     * @return A list of all lifestyle deposit entities in the cache
     */
    List<LifestyleDepositsEntity> getAllLifestyles();

    /**
     * Initializes the cache with data from the database.
     * <p>
     * This method should be called during application startup to populate
     * the cache with current lifestyle data from the persistent store.
     * </p>
     */
    void initializeCache();

    /**
     * Refreshes the cache with the latest data from the database.
     * <p>
     * Clears existing cache entries and repopulates from the database.
     * This method can be called manually via an admin endpoint or
     * automatically on a schedule.
     * </p>
     */
    void refreshCache();

    /**
     * Checks if the cache is operational and contains data.
     * <p>
     * Used for health checks and monitoring.
     * </p>
     *
     * @return true if the cache is operational and contains data, false otherwise
     */
    boolean isCacheHealthy();
}
