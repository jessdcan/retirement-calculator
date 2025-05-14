package com.example.retirementCalculator.exception;

/**
 * Exception thrown when an error occurs during calculation.
 * <p>
 * This exception is thrown when a mathematical error or overflow occurs
 * during the retirement savings calculation process.
 * </p>
 */
public class CalculationException extends RetirementCalculatorException {

    /**
     * Constructs a new calculation exception.
     *
     * @param message the detail message
     */
    public CalculationException(String message) {
        super(message);
    }

    /**
     * Constructs a new calculation exception with a cause.
     *
     * @param message the detail message
     * @param cause   the cause
     */
    public CalculationException(String message, Throwable cause) {
        super(message, cause);
    }
}
