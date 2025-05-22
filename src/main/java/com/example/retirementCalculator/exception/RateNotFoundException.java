package com.example.retirementCalculator.exception;

/**
 * Exception thrown when an interest rate cannot be found.
 * <p>
 * This exception is thrown when attempting to retrieve an interest rate
 * for a lifestyle type that doesn't exist in the cache.
 * </p>
 */
public class RateNotFoundException extends RetirementCalculatorException {

    /**
     * Constructs a new rate not found exception.
     *
     * @param message the detail message
     */
    public RateNotFoundException(String message) {
        super(message);
    }

    /**
     * Constructs a new rate not found exception with a cause.
     *
     * @param message the detail message
     * @param cause the cause
     */
    public RateNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
} 