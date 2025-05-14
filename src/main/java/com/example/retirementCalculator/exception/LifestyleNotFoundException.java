package com.example.retirementCalculator.exception;

/**
 * Exception thrown when a specified lifestyle type is not found.
 * <p>
 * This exception is thrown when attempting to retrieve a non-existent lifestyle type
 * from the cache or database.
 * </p>
 */
public class LifestyleNotFoundException extends RetirementCalculatorException {

    /**
     * Constructs a new lifestyle not found exception.
     *
     * @param lifestyleType the lifestyle type that was not found
     */
    public LifestyleNotFoundException(String lifestyleType) {
        super("Lifestyle type not found: " + lifestyleType);
    }
}
