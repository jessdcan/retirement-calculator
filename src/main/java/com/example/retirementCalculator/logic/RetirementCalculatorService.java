package com.example.retirementCalculator.logic;

import com.example.retirementCalculator.api.dto.RetirementCalculatorRequestDTO;
import com.example.retirementCalculator.api.dto.RetirementCalculatorResponseDTO;

import java.math.BigDecimal;

/**
 * Interface for the retirement calculator service.
 * <p>
 * Defines the contract for retirement savings calculations based on user input parameters.
 * Implementations should handle validation, calculation logic, and data retrieval.
 * </p>
 *
 * @author Your Name
 * @version 1.0
 * @since 1.0
 */
public interface RetirementCalculatorService {
    /**
     * Calculates the retirement savings and related metrics based on input parameters.
     * <p>
     * This method performs retirement savings calculations using the supplied parameters.
     * It fetches lifestyle data from cache/database, applies future value formulas,
     * and returns comprehensive results.
     * </p>
     *
     * @param request The DTO containing calculation parameters (current age, retirement age, interest rate, lifestyle type)
     * @return A response DTO containing the calculation results
     * @throws com.example.retirementCalculator.exception.LifestyleNotFoundException if the specified lifestyle type is not found
     * @throws com.example.retirementCalculator.exception.CalculationException       if an error occurs during the calculation process
     * @throws com.example.retirementCalculator.exception.InvalidParameterException  if input parameters are logically invalid (e.g., retirement age <= current age)
     */
    RetirementCalculatorResponseDTO calculateRetirementSavings(RetirementCalculatorRequestDTO request);
}