package com.example.retirementCalculator.exception;

/**
 * Exception thrown when retirement calculation parameters are invalid.
 * <p>
 * This exception is thrown when the input parameters for a retirement calculation
 * fail validation checks.
 * </p>
 */
public class InvalidCalculationException extends RetirementCalculatorException {

    /**
     * Constructs a new invalid calculation exception.
     *
     * @param message the detail message
     */
    public InvalidCalculationException(String message) {
        super(message);
    }

    /**
     * Constructs a new invalid calculation exception with a cause.
     *
     * @param message the detail message
     * @param cause the cause
     */
    public InvalidCalculationException(String message, Throwable cause) {
        super(message, cause);
    }
} 