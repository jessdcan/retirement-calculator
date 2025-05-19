package com.example.retirementCalculator.api.controllers;

import com.example.retirementCalculator.api.dto.RetirementCalculatorRequestDTO;
import com.example.retirementCalculator.api.dto.RetirementCalculatorResponseDTO;
import com.example.retirementCalculator.logic.RetirementCalculatorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for retirement calculator operations.
 * <p>
 * Provides endpoints for calculating retirement savings based on user input.
 * Supports both JSON and XML formats for request and response.
 * </p>
 *
 * @author Your Name
 * @version 1.0
 * @since 1.0
 */
@RestController
@RequestMapping("/api/v1/calculator")
@RequiredArgsConstructor
@Tag(name = "Retirement Calculator", description = "API endpoints for retirement savings calculations")
public class RetirementCalculatorController {

    private Logger log = org.slf4j.LoggerFactory.getLogger(RetirementCalculatorController.class);

    private final RetirementCalculatorService calculatorService;

    /**
     * Calculates retirement savings based on provided parameters.
     * <p>
     * This endpoint accepts retirement calculation parameters and returns
     * detailed projections for retirement savings.
     * </p>
     *
     * @param request The calculation request parameters
     * @return A response containing the calculation results
     */
    @Operation(
            summary = "Calculate retirement savings",
            description = "Calculates future retirement savings based on current age, retirement age, interest rate, and lifestyle preferences"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Calculation completed successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = RetirementCalculatorResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input parameters",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Specified lifestyle type not found",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error during calculation",
                    content = @Content
            )
    })
    @PostMapping(
            value = "/retirement",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
    )
    public ResponseEntity<RetirementCalculatorResponseDTO> calculateRetirement(
            @Parameter(description = "Retirement calculation parameters", required = true)
            @Valid @RequestBody RetirementCalculatorRequestDTO request) {

        log.info("Received retirement calculation request for age: {}, retirement age: {}, lifestyle: {}",
                request.getCurrentAge(), request.getRetirementAge(), request.getLifestyleType());

        RetirementCalculatorResponseDTO response = calculatorService.calculateRetirementSavings(request);

        log.info("Calculation completed successfully. Total retirement savings: {}", response.getTotalRetirementSavings());

        return ResponseEntity.ok(response);
    }

    /**
     * Health check endpoint for the calculator API.
     * <p>
     * Simple endpoint to verify the API is up and running.
     * </p>
     *
     * @return A success message indicating the API is operational
     */
    @Operation(
            summary = "API health check",
            description = "Simple endpoint to verify API is operational"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "API is operational",
                    content = @Content(
                            mediaType = "text/plain",
                            schema = @Schema(type = "string")
                    )
            )
    })
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        log.debug("Health check endpoint called");
        return ResponseEntity.ok("Retirement Calculator API is operational");
    }
}