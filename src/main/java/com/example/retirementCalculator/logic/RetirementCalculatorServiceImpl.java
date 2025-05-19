package com.example.retirementCalculator.logic;

import com.example.retirementCalculator.api.dto.RetirementCalculatorRequestDTO;
import com.example.retirementCalculator.api.dto.RetirementCalculatorResponseDTO;
import com.example.retirementCalculator.cache.LifestyleCacheService;
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

        try {
            validateParameters(request);
        } catch (InvalidParameterException e) {
            log.error("Validation error: {}", e.getMessage());
            throw e;
        }

        // Calculate the number of years until retirement
        // Will be n >= 0 if validation is successful
        int years = request.getRetirementAge() - request.getCurrentAge();

        LifestyleDepositsEntity lifestyleEntity = lifestyleCacheService.getLifestyleByType(request.getLifestyleType())
                .orElseThrow(() -> new LifestyleNotFoundException(request.getLifestyleType()));
        BigDecimal monthlyDeposit = lifestyleEntity.getMonthlyDeposit();

        log.info("Starting future value calculation: annualInterestRate={}, years={}, monthlyDeposit={}",
                request.getInterestRate(), years, monthlyDeposit);

        double monthlyRate = request.getInterestRate() / 100 / MONTHS_IN_YEAR;
        int months = years * MONTHS_IN_YEAR;

        BigDecimal futureValue = calculateFutureValue(monthlyDeposit, monthlyRate, months);

        log.info("Calculation complete: futureValue={}", futureValue);

        RetirementCalculatorResponseDTO responseDTO = new RetirementCalculatorResponseDTO();

        return responseDTO.builder()
                .totalRetirementSavings(futureValue.setScale(RESULT_SCALE, RoundingMode.HALF_UP))
                .yearsToRetirement(years)
                .monthlyDeposit(monthlyDeposit)
                .interestRate(request.getInterestRate())
                .lifestyleType(request.getLifestyleType())
                .build();
    }

    /**
     * Calculates the future value of a series of monthly deposits.
     * <p>
     * Uses the formula for the future value of a series of cash flows
     * compounded at a given interest rate.
     * </p>
     * <p>
     *     * Formula: FV = P * (((1 + r)^n - 1) / r)
     * </p>
     * <p>
     *     Also accounts for the case where the interest rate is zero.
     *     * In that case, the future value is simply the total of all deposits.
     * </p>
     *
     * @param monthlyDeposit The amount deposited each month
     * @param monthlyRate    The monthly interest rate (annual rate / 12)
     * @param months         The total number of months until retirement
     * @return The future value of the deposits
     */
    private BigDecimal calculateFutureValue(BigDecimal monthlyDeposit, double monthlyRate, int months) {
        // Handle the zero interest rate case
        if (monthlyRate == 0.0 || Math.abs(monthlyRate) < 1e-10) {
            // With zero interest, future value is simply deposit × number of months
            return monthlyDeposit.multiply(BigDecimal.valueOf(months));
        }

        // Standard compound interest formula for non-zero rates
        return monthlyDeposit
                .multiply(
                        BigDecimal.ONE.add(BigDecimal.valueOf(monthlyRate)).pow(months, MATH_CONTEXT).subtract(BigDecimal.ONE)
                )
                .divide(BigDecimal.valueOf(monthlyRate), MATH_CONTEXT);
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

}