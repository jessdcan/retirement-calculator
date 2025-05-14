package com.example.retirementCalculator.exception;

/**
 * Base exception class for retirement calculator application.
 * <p>
 * Serves as the parent exception for all application-specific exceptions.
 * </p>
 *
 * @author Your Name
 * @version 1.0
 * @since 1.0
 */
public class RetirementCalculatorException extends RuntimeException {

    /**
     * Constructs a new retirement calculator exception with the specified detail message.
     *
     * @param message the detail message
     */
    public RetirementCalculatorException(String message) {
        super(message);
    }

    /**
     * Constructs a new retirement calculator exception with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause
     */
    public RetirementCalculatorException(String message, Throwable cause) {
        super(message, cause);
    }
}

