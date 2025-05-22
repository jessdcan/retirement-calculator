package com.example.retirementCalculator.api.controllers;

import com.example.retirementCalculator.api.dto.RetirementCalculatorRequestDTO;
import com.example.retirementCalculator.api.dto.RetirementCalculatorResponseDTO;
import com.example.retirementCalculator.exception.InvalidParameterException;
import com.example.retirementCalculator.exception.LifestyleNotFoundException;
import com.example.retirementCalculator.logic.RetirementCalculatorService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RetirementCalculatorController.class)
public class RetirementCalculatorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RetirementCalculatorService calculatorService;

    @Autowired
    private ObjectMapper objectMapper;

    private RetirementCalculatorRequestDTO validRequest;
    private RetirementCalculatorResponseDTO successResponse;

    @BeforeEach
    void setUp() {
        // Setup valid request
        validRequest = RetirementCalculatorRequestDTO.builder()
                .currentAge(30)
                .retirementAge(65)
                .lifestyleType("simple")
                .build();

        // Setup success response
        successResponse = RetirementCalculatorResponseDTO.builder()
                .currentAge(30)
                .retirementAge(65)
                .interestRate(5.0)
                .lifestyleType("simple")
                .totalRetirementSavings(new BigDecimal("1191019.42"))
                .monthlyDeposit(new BigDecimal("2000.00"))
                .yearsToRetirement(35)
                .build();
    }

    @Test
    @DisplayName("Health check endpoint should return 200 OK")
    void healthCheckShouldReturnOk() throws Exception {
        mockMvc.perform(get("/api/v1/calculator/health"))
                .andExpect(status().isOk())
                .andExpect(content().string("Retirement Calculator API is operational"));
    }

    @Test
    @DisplayName("Calculate retirement with valid request should return 200 OK")
    void calculateRetirementWithValidRequestShouldReturnOk() throws Exception {
        // Mock service response
        when(calculatorService.calculateRetirementSavings(any(RetirementCalculatorRequestDTO.class)))
                .thenReturn(successResponse);

        // Perform request and validate response
        mockMvc.perform(post("/api/v1/calculator/retirement")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentAge", is(30)))
                .andExpect(jsonPath("$.retirementAge", is(65)))
                .andExpect(jsonPath("$.interestRate", is(5.0)))
                .andExpect(jsonPath("$.lifestyleType", is("simple")))
                .andExpect(jsonPath("$.totalRetirementSavings", is(1191019.42)))
                .andExpect(jsonPath("$.monthlyDeposit", is(2000.00)))
                .andExpect(jsonPath("$.yearsToRetirement", is(35)));
    }

    @Test
    @DisplayName("Calculate retirement with invalid parameters should return 400 Bad Request")
    void calculateRetirementWithInvalidParametersShouldReturnBadRequest() throws Exception {
        // Mock service throwing exception for invalid parameters
        when(calculatorService.calculateRetirementSavings(any(RetirementCalculatorRequestDTO.class)))
                .thenThrow(new InvalidParameterException("Retirement age must be greater than current age"));

        // Perform request and validate response
        mockMvc.perform(post("/api/v1/calculator/retirement")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Calculate retirement with non-existent lifestyle should return 404 Not Found")
    void calculateRetirementWithNonExistentLifestyleShouldReturnNotFound() throws Exception {
        // Mock service throwing exception for non-existent lifestyle
        when(calculatorService.calculateRetirementSavings(any(RetirementCalculatorRequestDTO.class)))
                .thenThrow(new LifestyleNotFoundException("Lifestyle type not found: nonexistent"));

        // Perform request and validate response
        mockMvc.perform(post("/api/v1/calculator/retirement")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isNotFound());
    }

   // @Test
    @DisplayName("Calculate retirement with XML request should work correctly")
    void calculateRetirementWithXmlRequestShouldWorkCorrectly() throws Exception {
        // Mock service response
        when(calculatorService.calculateRetirementSavings(any(RetirementCalculatorRequestDTO.class)))
                .thenReturn(successResponse);

        // Create XML request content (simplified for test)
        String xmlRequest = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<RetirementCalculatorRequestDTO>" +
                "    <currentAge>30</currentAge>" +
                "    <retirementAge>65</retirementAge>" +
                "    <interestRate>5.0</interestRate>" +
                "    <lifestyleType>simple</lifestyleType>" +
                "</RetirementCalculatorRequestDTO>";

        // Perform request and validate response
        mockMvc.perform(post("/api/v1/calculator/retirement")
                        .contentType(MediaType.APPLICATION_XML)
                        .accept(MediaType.APPLICATION_XML)
                        .content(xmlRequest))
                .andExpect(status().isOk())
                .andExpect(xpath("/RetirementCalculatorResponseDTO/currentAge").string("30"))
                .andExpect(xpath("/RetirementCalculatorResponseDTO/retirementAge").string("65"))
                .andExpect(xpath("/RetirementCalculatorResponseDTO/lifestyleType").string("simple"));
    }
}