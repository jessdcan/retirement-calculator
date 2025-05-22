package com.example.retirementCalculator.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;

/**
 * Data Transfer Object for retirement calculation requests.
 * <p>
 * Contains validated input parameters required for retirement savings calculations.
 * This DTO is used to receive data from API clients and pass it to the domain layer.
 * Interest rates are now handled internally by the domain layer based on lifestyle type.
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
     * Optional custom interest rate to use for the calculation.
     * If not provided, the system will use the interest rate from the cache based on lifestyle type.
     */
    @Min(value = 0, message = "Interest rate must be greater than or equal to 0")
    @Max(value = 100, message = "Interest rate must be less than or equal to 100")
    @Schema(description = "Optional custom interest rate as a percentage", example = "5.5")
    private BigDecimal customInterestRate;

    /**
     * The desired lifestyle type for retirement planning.
     * <p>
     * Must be a non-empty string.
     * Typical values include "simple", "fancy", "modest", "comfortable".
     * The interest rate will be determined based on this lifestyle type.
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
