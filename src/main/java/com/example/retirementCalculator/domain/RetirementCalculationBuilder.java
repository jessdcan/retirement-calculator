package com.example.retirementCalculator.domain;

import com.example.retirementCalculator.cache.InterestRateCacheService;
import com.example.retirementCalculator.cache.LifestyleCacheService;
import com.example.retirementCalculator.exception.InvalidCalculationException;
import com.example.retirementCalculator.exception.LifestyleNotFoundException;
import com.example.retirementCalculator.exception.RateNotFoundException;
import com.example.retirementCalculator.persistance.entities.LifestyleDepositsEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Builder class for constructing RetirementCalculation objects.
 * <p>
 * This class handles the construction of RetirementCalculation objects from various sources,
 * including request DTOs and other input formats. It also handles the retrieval of
 * interest rates and monthly deposits from the cache.
 * </p>
 */
@Component
@RequiredArgsConstructor
public class RetirementCalculationBuilder {

    private final InterestRateCacheService interestRateCacheService;
    private final LifestyleCacheService lifestyleCacheService;

    /**
     * Builds a RetirementCalculation from the provided parameters.
     *
     * @param currentAge the current age
     * @param retirementAge the retirement age
     * @param lifestyleType the lifestyle type
     * @return a new RetirementCalculation instance
     * @throws InvalidCalculationException if the calculation parameters are invalid
     * @throws LifestyleNotFoundException if the lifestyle type is not found
     * @throws RateNotFoundException if the interest rate is not found
     */
    public RetirementCalculation build(int currentAge, int retirementAge, String lifestyleType) {
        // Get interest rate from cache
        BigDecimal interestRate = interestRateCacheService.getInterestRateByLifestyleType(lifestyleType)
                .orElseThrow(() -> new RateNotFoundException("Interest rate not found for lifestyle type: " + lifestyleType));

        // Get monthly deposit from cache
        LifestyleDepositsEntity lifestyle = lifestyleCacheService.getLifestyleByType(lifestyleType)
                .orElseThrow(() -> new LifestyleNotFoundException("Lifestyle not found: " + lifestyleType));

        // Build and validate the calculation
        RetirementCalculation calculation = RetirementCalculation.builder()
                .currentAge(currentAge)
                .retirementAge(retirementAge)
                .lifestyleType(lifestyleType)
                .interestRate(interestRate)
                .monthlyDeposit(lifestyle.getMonthlyDeposit())
                .build();

        calculation.validate();
        return calculation;
    }

    /**
     * Builds a RetirementCalculation from the provided parameters with an optional custom interest rate.
     *
     * @param currentAge the current age
     * @param retirementAge the retirement age
     * @param lifestyleType the lifestyle type
     * @param customInterestRate optional custom interest rate to use instead of the cached rate
     * @return a new RetirementCalculation instance
     * @throws InvalidCalculationException if the calculation parameters are invalid
     * @throws LifestyleNotFoundException if the lifestyle type is not found
     * @throws RateNotFoundException if the interest rate is not found and no custom rate is provided
     */
    public RetirementCalculation build(int currentAge, int retirementAge, String lifestyleType, BigDecimal customInterestRate) {
        // Get monthly deposit from cache
        LifestyleDepositsEntity lifestyle = lifestyleCacheService.getLifestyleByType(lifestyleType)
                .orElseThrow(() -> new LifestyleNotFoundException("Lifestyle not found: " + lifestyleType));

        // Use custom interest rate if provided, otherwise get from cache
        BigDecimal interestRate = customInterestRate != null ? customInterestRate :
                interestRateCacheService.getInterestRateByLifestyleType(lifestyleType)
                        .orElseThrow(() -> new RateNotFoundException("Interest rate not found for lifestyle type: " + lifestyleType));

        // Build and validate the calculation
        RetirementCalculation calculation = RetirementCalculation.builder()
                .currentAge(currentAge)
                .retirementAge(retirementAge)
                .lifestyleType(lifestyleType)
                .interestRate(interestRate)
                .monthlyDeposit(lifestyle.getMonthlyDeposit())
                .build();

        calculation.validate();
        return calculation;
    }
} 