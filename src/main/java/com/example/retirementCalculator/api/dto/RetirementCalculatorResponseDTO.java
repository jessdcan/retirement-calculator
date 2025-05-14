package com.example.retirementCalculator.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Data Transfer Object for retirement calculation responses.
 * <p>
 * Contains the calculated retirement savings information and related parameters.
 * This DTO is used to send calculation results back to API clients.
 * </p>
 *
 * @author Your Name
 * @version 1.0
 * @since 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Response object containing retirement calculation results")
public class RetirementCalculatorResponseDTO {

    /**
     * The current age used in the calculation.
     */
    @Schema(description = "Current age in years used in calculation", example = "30")
    private Integer currentAge;

    /**
     * The retirement age used in the calculation.
     */
    @Schema(description = "Retirement age in years used in calculation", example = "65")
    private Integer retirementAge;

    /**
     * The annual interest rate used as a percentage.
     */
    @Schema(description = "Annual interest rate as a percentage used in calculation", example = "5.5")
    private Double interestRate;

    /**
     * The lifestyle type used in the calculation.
     */
    @Schema(description = "Lifestyle type used in calculation", example = "comfortable")
    private String lifestyleType;

    /**
     * The calculated total retirement savings at retirement age.
     */
    @Schema(description = "Total projected retirement savings at retirement age", example = "1250000.00")
    private BigDecimal totalRetirementSavings;

    /**
     * The monthly deposit amount needed to reach the retirement goal.
     */
    @Schema(description = "Required monthly deposit to reach retirement goal", example = "1000.00")
    private BigDecimal monthlyDeposit;

    /**
     * The annual expenses expected during retirement based on the lifestyle choice.
     */
    @Schema(description = "Expected annual expenses during retirement", example = "50000.00")
    private BigDecimal annualExpenses;

    /**
     * The number of years the retirement savings will last.
     */
    @Schema(description = "Number of years retirement savings will last", example = "25")
    private Integer yearsOfRetirement;

    /**
     * The percentage of retirement goal achieved.
     */
    @Schema(description = "Percentage of retirement goal achieved", example = "100.0")
    private Double percentageOfGoalAchieved;
}