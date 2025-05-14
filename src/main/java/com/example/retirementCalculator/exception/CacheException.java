package com.example.retirementCalculator.exception;

/**
 * Exception thrown when the cache is unavailable or fails.
 * <p>
 * This exception is thrown when there is an issue with the Redis cache
 * that prevents data retrieval or initialization.
 * </p>
 */
public class CacheException extends RetirementCalculatorException {

    /**
     * Constructs a new cache exception.
     *
     * @param message the detail message
     */
    public CacheException(String message) {
        super(message);
    }

    /**
     * Constructs a new cache exception with a cause.
     *
     * @param message the detail message
     * @param cause the cause
     */
    public CacheException(String message, Throwable cause) {
        super(message, cause);
    }
}
