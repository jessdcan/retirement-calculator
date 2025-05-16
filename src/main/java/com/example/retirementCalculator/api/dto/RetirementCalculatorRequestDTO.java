package com.example.retirementCalculator.api.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * Data Transfer Object for retirement calculation requests.
 * <p>
 * Contains validated input parameters required for retirement savings calculations.
 * This DTO is used to receive data from API clients and pass it to the calculator service.
 * </p>
 *
 * @author Your Name
 * @version 1.0
 * @since 1.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request object for retirement savings calculations")
public class RetirementCalculatorRequestDTO {

    /**
     * The current age of the person in years.
     * <p>
     * Must be between 18 and 100 years.
     * </p>
     */
    @NotNull(message = "Current age is required")
    @Min(value = 18, message = "Current age must be at least 18 years")
    @Max(value = 100, message = "Current age must be less than 100 years")
    @Schema(description = "Current age in years", example = "30", required = true)
    private Integer currentAge;

    /**
     * The expected retirement age in years.
     * <p>
     * Must be greater than current age and less than or equal to 100 years.
     * </p>
     */
    @NotNull(message = "Retirement age is required")
    @Min(value = 18, message = "Retirement age must be at least 18 years")
    @Max(value = 100, message = "Retirement age must be less than or equal to 100 years")
    @Schema(description = "Expected retirement age in years", example = "65", required = true)
    private Integer retirementAge;

    /**
     * The expected annual interest rate as a percentage.
     * <p>
     * Must be a positive number between 0 and 20.
     * </p>
     */
    @NotNull(message = "Interest rate is required")
    @DecimalMin(value = "0.0", message = "Interest rate must be greater than or equal to 0")
    @DecimalMax(value = "20.0", message = "Interest rate must be less than or equal to 20")
    @Schema(description = "Expected annual interest rate as a percentage", example = "5.5", required = true)
    private Double interestRate;

    /**
     * The desired lifestyle type for retirement planning.
     * <p>
     * Must be a non-empty string.
     * Typical values include "simple", "fancy", "modest", "comfortable".
     * </p>
     */
    @NotBlank(message = "Lifestyle type is required")
    @Schema(description = "Desired lifestyle type for retirement (e.g., simple, fancy)", example = "comfortable", required = true)
    private String lifestyleType;

//    /**
//     * The annual expenses expected during retirement based on the lifestyle choice.
//     */
//    @Schema(description = "Expected annual expenses during retirement", example = "50000.00")
//    private BigDecimal annualExpenses;
}
