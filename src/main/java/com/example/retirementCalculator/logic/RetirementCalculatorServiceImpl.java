package com.example.retirementCalculator.logic;

import com.example.retirementCalculator.api.dto.RetirementCalculatorRequestDTO;
import com.example.retirementCalculator.api.dto.RetirementCalculatorResponseDTO;
import com.example.retirementCalculator.cache.LifestyleCacheService;
import com.example.retirementCalculator.exception.CalculationException;
import com.example.retirementCalculator.exception.InvalidParameterException;
import com.example.retirementCalculator.exception.LifestyleNotFoundException;
import com.example.retirementCalculator.persistance.entities.LifestyleDepositsEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * Implementation of the retirement calculator service.
 * <p>
 * Performs retirement savings calculations based on user input parameters.
 * Uses the lifestyle cache service to retrieve lifestyle-specific data.
 * </p>
 *
 * @author Your Name
 * @version 1.0
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RetirementCalculatorServiceImpl implements RetirementCalculatorService {

    private static final int MONTHS_IN_YEAR = 12;
    private static final int CALCULATION_SCALE = 10;
    private static final int RESULT_SCALE = 2;
    private static final MathContext MATH_CONTEXT = new MathContext(CALCULATION_SCALE, RoundingMode.HALF_UP);

    private final LifestyleCacheService lifestyleCacheService;

    /**
     * {@inheritDoc}
     */
    @Override
    public RetirementCalculatorResponseDTO calculateRetirementSavings(RetirementCalculatorRequestDTO request) {
        log.info("Calculating retirement savings for request: age={}, retirementAge={}, interestRate={}, lifestyle={}",
                request.getCurrentAge(), request.getRetirementAge(), request.getInterestRate(), request.getLifestyleType());

        // Validate request parameters beyond basic validation annotations
        validateParameters(request);

        try {
            // Retrieve lifestyle data from cache
            LifestyleDepositsEntity lifestyle = lifestyleCacheService.getLifestyleByType(request.getLifestyleType())
                    .orElseThrow(() -> new LifestyleNotFoundException(request.getLifestyleType()));

            log.debug("Retrieved lifestyle data: monthlyDeposit={}, annualExpenses={}",
                    lifestyle.getMonthlyDeposit(), lifestyle.getAnnualExpenses());

            // Calculate future value of the investment
            int yearsToRetirement = request.getRetirementAge() - request.getCurrentAge();
            int monthsToRetirement = yearsToRetirement * MONTHS_IN_YEAR;

            BigDecimal monthlyRate = convertAnnualRateToMonthly(request.getInterestRate());

            // Future value of regular monthly deposits
            BigDecimal totalRetirementSavings = calculateFutureValue(
                    lifestyle.getMonthlyDeposit(),
                    monthlyRate,
                    monthsToRetirement
            );

            // Calculate how long savings will last
            int yearsOfRetirement = calculateYearsOfRetirement(
                    totalRetirementSavings,
                    lifestyle.getAnnualExpenses(),
                    request.getInterestRate()
            );

            // Calculate percentage of goal achieved (assuming 100% in this implementation)
            double percentageOfGoalAchieved = 100.0;

            log.info("Calculation completed successfully. Total retirement savings: {}", totalRetirementSavings);

            // Build and return response
            return RetirementCalculatorResponseDTO.builder()
                    .currentAge(request.getCurrentAge())
                    .retirementAge(request.getRetirementAge())
                    .interestRate(request.getInterestRate())
                    .lifestyleType(request.getLifestyleType())
                    .totalRetirementSavings(totalRetirementSavings.setScale(RESULT_SCALE, RoundingMode.HALF_UP))
                    .monthlyDeposit(lifestyle.getMonthlyDeposit())
                    .annualExpenses(lifestyle.getAnnualExpenses())
                    .yearsOfRetirement(yearsOfRetirement)
                    .percentageOfGoalAchieved(percentageOfGoalAchieved)
                    .build();

        } catch (LifestyleNotFoundException e) {
            throw e;
        } catch (ArithmeticException | IllegalArgumentException e) {
            log.error("Calculation error: {}", e.getMessage(), e);
            throw new CalculationException("Error during retirement calculation: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error during calculation: {}", e.getMessage(), e);
            throw new CalculationException("Unexpected error during retirement calculation", e);
        }
    }

    /**
     * Validates the retirement calculator request parameters for logical consistency.
     * <p>
     * Checks if retirement age is greater than current age and other logical validations
     * beyond basic validation annotations.
     * </p>
     *
     * @param request The calculation request to validate
     * @throws InvalidParameterException if parameters are logically invalid
     */
    private void validateParameters(RetirementCalculatorRequestDTO request) {
        log.debug("Validating request parameters");

        if (request.getRetirementAge() <= request.getCurrentAge()) {
            String errorMessage = "Retirement age must be greater than current age";
            log.error("Validation error: {}", errorMessage);
            throw new InvalidParameterException(errorMessage);
        }

        if (request.getInterestRate() < 0) {
            String errorMessage = "Interest rate cannot be negative";
            log.error("Validation error: {}", errorMessage);
            throw new InvalidParameterException(errorMessage);
        }

        int yearsToRetirement = request.getRetirementAge() - request.getCurrentAge();
        if (yearsToRetirement > 100) {
            String errorMessage = "Years to retirement cannot exceed 100";
            log.error("Validation error: {}", errorMessage);
            throw new InvalidParameterException(errorMessage);
        }

        log.debug("Parameter validation passed");
    }

    /**
     * Converts an annual interest rate to a monthly interest rate.
     *
     * @param annualRatePercent Annual interest rate as a percentage
     * @return Monthly interest rate as a decimal
     */
    private BigDecimal convertAnnualRateToMonthly(double annualRatePercent) {
        // Convert percentage to decimal: 5% -> 0.05
        BigDecimal annualRateDecimal = BigDecimal.valueOf(annualRatePercent / 100.0);

        // Convert annual rate to monthly: (1+r)^(1/12) - 1
        BigDecimal monthlyRate = BigDecimal.ONE
                .add(annualRateDecimal)
                .pow(1, MATH_CONTEXT)
                .divide(BigDecimal.valueOf(MONTHS_IN_YEAR), MATH_CONTEXT)
                .subtract(BigDecimal.ONE);

        log.debug("Converted annual interest rate {}% to monthly rate {}",
                annualRatePercent, monthlyRate.multiply(BigDecimal.valueOf(100)));

        return monthlyRate;
    }

    /**
     * Calculates the future value of periodic payments.
     * <p>
     * Uses the compound interest formula for periodic payments:
     * FV = PMT × ((1 + r)^n - 1) / r
     * </p>
     *
     * @param monthlyDeposit The monthly deposit amount
     * @param monthlyRate The monthly interest rate as a decimal
     * @param months The number of months
     * @return The future value of the investment
     */
    private BigDecimal calculateFutureValue(BigDecimal monthlyDeposit, BigDecimal monthlyRate, int months) {
        try {
            log.debug("Calculating future value for monthly deposit={}, rate={}, months={}",
                    monthlyDeposit, monthlyRate, months);

            // Handle edge case of zero interest rate
            if (monthlyRate.compareTo(BigDecimal.ZERO) == 0) {
                BigDecimal result = monthlyDeposit.multiply(BigDecimal.valueOf(months));
                log.debug("Zero interest rate calculation, result: {}", result);
                return result;
            }

            // FV = PMT × ((1 + r)^n - 1) / r
            BigDecimal onePlusRate = BigDecimal.ONE.add(monthlyRate);
            BigDecimal compoundingFactor = onePlusRate.pow(months, MATH_CONTEXT).subtract(BigDecimal.ONE);
            BigDecimal futureValue = monthlyDeposit
                    .multiply(compoundingFactor)
                    .divide(monthlyRate, MATH_CONTEXT);

            log.debug("Future value calculation result: {}", futureValue);
            return futureValue;

        } catch (ArithmeticException e) {
            log.error("Arithmetic error during future value calculation: {}", e.getMessage(), e);
            throw new CalculationException("Error calculating future value: " + e.getMessage(), e);
        }
    }

    /**
     * Calculates how many years the retirement savings will last.
     * <p>
     * Assumes a constant withdrawal rate (annual expenses) and remaining interest on balance.
     * </p>
     *
     * @param initialBalance The starting retirement savings balance
     * @param annualExpenses The yearly withdrawal amount
     * @param annualInterestRate The annual interest rate as a percentage
     * @return The number of years the savings will last
     */
    private int calculateYearsOfRetirement(BigDecimal initialBalance, BigDecimal annualExpenses, double annualInterestRate) {
        try {
            log.debug("Calculating years of retirement for balance={}, expenses={}, interestRate={}%",
                    initialBalance, annualExpenses, annualInterestRate);

            // Convert annual interest rate to decimal
            BigDecimal annualRate = BigDecimal.valueOf(annualInterestRate / 100.0);

            // If expenses are zero or negative, savings last forever
            if (annualExpenses.compareTo(BigDecimal.ZERO) <= 0) {
                log.debug("Annual expenses are zero or negative, returning maximum years (100)");
                return 100;
            }

            // If interest rate is zero, simple division
            if (annualRate.compareTo(BigDecimal.ZERO) == 0) {
                int years = initialBalance.divide(annualExpenses, 0, RoundingMode.FLOOR).intValue();
                log.debug("Zero interest rate, savings will last {} years", years);
                return years;
            }

            // If annual interest earned exceeds annual expenses, savings last forever
            // (capped at 100 years for practicality)
            BigDecimal annualInterest = initialBalance.multiply(annualRate);
            if (annualInterest.compareTo(annualExpenses) >= 0) {
                log.debug("Interest earned exceeds expenses, returning maximum years (100)");
                return 100;
            }

            // Otherwise, simulate year by year until funds are depleted
            BigDecimal balance = initialBalance;
            int years = 0;

            while (balance.compareTo(BigDecimal.ZERO) > 0 && years < 100) {
                // Calculate interest earned this year
                BigDecimal interestEarned = balance.multiply(annualRate);

                // Subtract expenses and add interest
                balance = balance.subtract(annualExpenses).add(interestEarned);
                years++;

                log.trace("Year {}: balance={}, interest={}", years, balance, interestEarned);
            }

            log.debug("Retirement savings will last {} years", years);
            return years;

        } catch (ArithmeticException e) {
            log.error("Arithmetic error during years of retirement calculation: {}", e.getMessage(), e);
            throw new CalculationException("Error calculating years of retirement: " + e.getMessage(), e);
        }
    }
}