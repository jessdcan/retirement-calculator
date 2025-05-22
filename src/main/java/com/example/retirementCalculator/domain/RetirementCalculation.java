package com.example.retirementCalculator.domain;

import com.example.retirementCalculator.exception.InvalidCalculationException;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

/**
 * Domain object representing a retirement calculation.
 * <p>
 * This class encapsulates all the business logic and data related to retirement calculations.
 * It is the central point for all retirement-related operations and validations.
 * </p>
 */
@Getter
@Builder
public class RetirementCalculation {
    private final int currentAge;
    private final int retirementAge;
    private final String lifestyleType;
    private final BigDecimal interestRate;
    private final BigDecimal monthlyDeposit;

    /**
     * Validates the retirement calculation parameters.
     *
     * @throws InvalidCalculationException if any validation fails
     */
    public void validate() {
        if (currentAge < 0) {
            throw new InvalidCalculationException("Current age cannot be negative");
        }
        if (retirementAge <= currentAge) {
            throw new InvalidCalculationException("Retirement age must be greater than current age");
        }
        if (lifestyleType == null || lifestyleType.trim().isEmpty()) {
            throw new InvalidCalculationException("Lifestyle type is required");
        }
        if (interestRate == null || interestRate.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidCalculationException("Interest rate must be positive");
        }
        if (monthlyDeposit == null || monthlyDeposit.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidCalculationException("Monthly deposit must be positive");
        }
    }

    /**
     * Calculates the future value of retirement savings.
     * <p>
     * Uses the formula: FV = PMT * (((1 + r)^n - 1) / r)
     * where:
     * PMT = monthly deposit
     * r = monthly interest rate
     * n = number of months until retirement
     * </p>
     *
     * @return the calculated future value
     */
    public BigDecimal calculateFutureValue() {
        // Convert annual interest rate to monthly
        BigDecimal monthlyRate = interestRate.divide(BigDecimal.valueOf(100), 8, RoundingMode.HALF_UP)
                .divide(BigDecimal.valueOf(12), 8, RoundingMode.HALF_UP);

        // Calculate number of months until retirement
        int monthsUntilRetirement = (retirementAge - currentAge) * 12;

        // Calculate (1 + r)^n
        BigDecimal onePlusRate = BigDecimal.ONE.add(monthlyRate);
        BigDecimal onePlusRateToN = onePlusRate.pow(monthsUntilRetirement);

        // Calculate ((1 + r)^n - 1) / r
        BigDecimal numerator = onePlusRateToN.subtract(BigDecimal.ONE);
        BigDecimal denominator = monthlyRate;
        BigDecimal multiplier = numerator.divide(denominator, 8, RoundingMode.HALF_UP);

        // Calculate final future value
        return monthlyDeposit.multiply(multiplier).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Returns a map of calculation results.
     *
     * @return map containing calculation results
     */
    public Map<String, Object> getResults() {
        Map<String, Object> results = new HashMap<>();
        results.put("currentAge", currentAge);
        results.put("retirementAge", retirementAge);
        results.put("lifestyleType", lifestyleType);
        results.put("interestRate", interestRate);
        results.put("monthlyDeposit", monthlyDeposit);
        results.put("futureValue", calculateFutureValue());
        return results;
    }
} 