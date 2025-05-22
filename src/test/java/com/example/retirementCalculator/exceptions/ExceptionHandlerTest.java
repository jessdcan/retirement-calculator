package com.example.retirementCalculator.exceptions;

import com.example.retirementCalculator.api.controllers.RetirementCalculatorController;
import com.example.retirementCalculator.api.dto.RetirementCalculatorRequestDTO;
import com.example.retirementCalculator.exception.CalculationException;
import com.example.retirementCalculator.exception.CacheException;
import com.example.retirementCalculator.exception.InvalidParameterException;
import com.example.retirementCalculator.exception.LifestyleNotFoundException;
import com.example.retirementCalculator.logic.RetirementCalculatorService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RetirementCalculatorController.class)
public class ExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RetirementCalculatorService calculatorService;

    @Autowired
    private ObjectMapper objectMapper;

    private RetirementCalculatorRequestDTO validRequest;

    @BeforeEach
    void setUp() {
        validRequest = RetirementCalculatorRequestDTO.builder()
                .currentAge(30)
                .retirementAge(65)
                .lifestyleType("simple")
                .build();
    }

    @Test
    @DisplayName("Calculate retirement with service throwing InvalidParameterException should return 400 with error response")
    void calculateRetirement_InvalidParameterException_ShouldReturnBadRequestWithErrorResponse() throws Exception {
        String errorMessage = "Retirement age must be greater than current age.";
        when(calculatorService.calculateRetirementSavings(any(RetirementCalculatorRequestDTO.class)))
                .thenThrow(new InvalidParameterException(errorMessage));


        mockMvc.perform(post("/api/v1/calculator/retirement")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Invalid Parameters")))
                .andExpect(jsonPath("$.message", is(errorMessage)))
                .andExpect(jsonPath("$.path", is("/api/v1/calculator/retirement")))
                .andExpect(jsonPath("$.fieldErrors").doesNotExist());
    }

//    @Test
    @DisplayName("Calculate retirement with service throwing LifestyleNotFoundException should return 404 with error response")
    void calculateRetirement_LifestyleNotFoundException_ShouldReturnNotFoundWithErrorResponse() throws Exception {
        String errorMessage = "Lifestyle type not found: premium.";
        when(calculatorService.calculateRetirementSavings(any(RetirementCalculatorRequestDTO.class)))
                .thenThrow(new LifestyleNotFoundException(errorMessage));

        mockMvc.perform(post("/api/v1/calculator/retirement")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Resource Not Found")))
                .andExpect(jsonPath("$.message", is(errorMessage)))
                .andExpect(jsonPath("$.path", is("/api/v1/calculator/retirement")))
                .andExpect(jsonPath("$.fieldErrors").doesNotExist());
    }

    @Test
    @DisplayName("Calculate retirement with service throwing CalculationException should return 500 with error response")
    void calculateRetirement_CalculationException_ShouldReturnInternalServerErrorWithErrorResponse() throws Exception {
        String errorMessage = "Error during retirement calculation.";
        when(calculatorService.calculateRetirementSavings(any(RetirementCalculatorRequestDTO.class)))
                .thenThrow(new CalculationException(errorMessage));

        mockMvc.perform(post("/api/v1/calculator/retirement")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status", is(500)))
                .andExpect(jsonPath("$.error", is("Calculation Error")))
                .andExpect(jsonPath("$.message", is(errorMessage)))
                .andExpect(jsonPath("$.path", is("/api/v1/calculator/retirement")))
                .andExpect(jsonPath("$.fieldErrors").doesNotExist());
    }

    @Test
    @DisplayName("Calculate retirement with service throwing CacheException should return 503 with error response")
    void calculateRetirement_CacheException_ShouldReturnServiceUnavailableWithErrorResponse() throws Exception {
        String errorMessage = "Cache service unavailable.";
        when(calculatorService.calculateRetirementSavings(any(RetirementCalculatorRequestDTO.class)))
                .thenThrow(new CacheException(errorMessage));

        mockMvc.perform(post("/api/v1/calculator/retirement")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status", is(503)))
                .andExpect(jsonPath("$.error", is("Service Unavailable")))
                .andExpect(jsonPath("$.message", is("Cache service is currently unavailable. Please try again later.")))
                .andExpect(jsonPath("$.path", is("/api/v1/calculator/retirement")))
                .andExpect(jsonPath("$.fieldErrors").doesNotExist());
    }

//    @Test
    @DisplayName("Calculate retirement with invalid request body should return 400 with validation error response")
    void calculateRetirement_InvalidRequestBody_ShouldReturnBadRequestWithFieldError() throws Exception {
        RetirementCalculatorRequestDTO invalidRequest = RetirementCalculatorRequestDTO.builder()
                .currentAge(18) // Invalid age
                .retirementAge(60)
                .lifestyleType(null) // Invalid lifestyle
                .build();

        mockMvc.perform(post("/api/v1/calculator/retirement")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Validation Error")))
                .andExpect(jsonPath("$.message", is("Request validation failed")))
                .andExpect(jsonPath("$.path", is("/api/v1/calculator/retirement")))
                .andExpect(jsonPath("$.fieldErrors", hasSize(2)))
                .andExpect(jsonPath("$.fieldErrors[0].field").value("currentAge"))
                .andExpect(jsonPath("$.fieldErrors[0].message").exists())
                .andExpect(jsonPath("$.fieldErrors[0].rejectedValue", is(15)))
                .andExpect(jsonPath("$.fieldErrors[1].field").value("lifestyleType"))
                .andExpect(jsonPath("$.fieldErrors[1].message").exists())
                .andExpect(jsonPath("$.fieldErrors[1].rejectedValue").value(null));
    }

//    @Test
    @DisplayName("Calculate retirement with ConstraintViolationException should return 400 with field error response")
    void calculateRetirement_ConstraintViolationException_ShouldReturnBadRequestWithFieldError() throws Exception {
        // Simulate a ConstraintViolationException during service layer validation
        when(calculatorService.calculateRetirementSavings(any()))
                .thenThrow(new ConstraintViolationException("Validation failed", Collections.emptySet()));

        // Create a request that might trigger this (though the mock throws it directly)
        RetirementCalculatorRequestDTO requestWithViolation = RetirementCalculatorRequestDTO.builder()
                .currentAge(10)
                .retirementAge(70)
                .lifestyleType("fancy")
                .build();

        mockMvc.perform(post("/api/v1/calculator/retirement")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestWithViolation)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Constraint Violation")))
                .andExpect(jsonPath("$.message", is("Validation constraints violated")))
                .andExpect(jsonPath("$.path", is("/api/v1/calculator/retirement")))
                .andExpect(jsonPath("$.fieldErrors").isArray()); // We expect an array of field errors, even if empty in this mock
    }

    @Test
    @DisplayName("Calculate retirement with generic Exception should return 500 with generic error response")
    void calculateRetirement_GenericException_ShouldReturnInternalServerErrorWithErrorResponse() throws Exception {
        String errorMessage = "An unexpected error occurred.";
        when(calculatorService.calculateRetirementSavings(any(RetirementCalculatorRequestDTO.class)))
                .thenThrow(new RuntimeException(errorMessage));

        mockMvc.perform(post("/api/v1/calculator/retirement")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status", is(500)))
                .andExpect(jsonPath("$.error", is("Internal Server Error")))
                .andExpect(jsonPath("$.message", is("An unexpected error occurred. Please try again later.")))
                .andExpect(jsonPath("$.path", is("/api/v1/calculator/retirement")))
                .andExpect(jsonPath("$.fieldErrors").doesNotExist());
    }
}