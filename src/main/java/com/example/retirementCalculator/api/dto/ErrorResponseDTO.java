package com.example.retirementCalculator.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Data Transfer Object for standardized API error responses.
 * <p>
 * Contains detailed error information that is returned to API clients
 * when an exception or validation error occurs during request processing.
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
@Schema(description = "Standardized error response for API exceptions")
public class ErrorResponseDTO {

    /**
     * The timestamp when the error occurred.
     */
    @Schema(description = "Timestamp when the error occurred", example = "2025-05-14T14:30:00")
    private LocalDateTime timestamp;

    /**
     * The HTTP status code associated with the error.
     */
    @Schema(description = "HTTP status code", example = "400")
    private Integer status;

    /**
     * A short, human-readable summary of the error.
     */
    @Schema(description = "Error message summary", example = "Validation Error")
    private String error;

    /**
     * A more detailed message explaining the error.
     */
    @Schema(description = "Detailed error message", example = "Request validation failed due to invalid input parameters")
    private String message;

    /**
     * The API path where the error occurred.
     */
    @Schema(description = "API endpoint path where the error occurred", example = "/api/v1/calculator/retirement")
    private String path;

    /**
     * List of field-specific validation errors, if applicable.
     * <p>
     * This field is populated when validation errors occur for specific request fields.
     * </p>
     */
    @Schema(description = "List of field-specific validation errors", nullable = true)
    private List<FieldErrorDto> fieldErrors;

    /**
     * Nested class representing field-specific validation errors.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "Field-specific validation error details")
    public static class FieldErrorDto {

        /**
         * The name of the field that failed validation.
         */
        @Schema(description = "Field name with validation error", example = "currentAge")
        private String field;

        /**
         * The rejected value that caused the validation error.
         */
        @Schema(description = "Rejected field value", example = "15")
        private Object rejectedValue;

        /**
         * The error message explaining why validation failed.
         */
        @Schema(description = "Validation error message", example = "Current age must be at least 18 years")
        private String message;
    }
}
