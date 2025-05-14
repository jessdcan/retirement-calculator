package com.example.retirementCalculator.exception;

/**
 * Exception thrown when input parameters are logically invalid.
 * <p>
 * This exception is thrown when input parameters pass basic validation but are logically
 * invalid (e.g., retirement age <= current age).
 * </p>
 */
public class InvalidParameterException extends RetirementCalculatorException {

    /**
     * Constructs a new invalid parameter exception.
     *
     * @param message the detail message
     */
    public InvalidParameterException(String message) {
        super(message);
    }
}
