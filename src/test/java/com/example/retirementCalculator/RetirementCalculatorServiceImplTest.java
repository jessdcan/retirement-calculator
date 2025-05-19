package com.example.retirementCalculator;

import com.example.retirementCalculator.api.dto.RetirementCalculatorRequestDTO;
import com.example.retirementCalculator.api.dto.RetirementCalculatorResponseDTO;
import com.example.retirementCalculator.cache.LifestyleCacheService;
import com.example.retirementCalculator.cache.LifestyleCacheServiceImpl;
import com.example.retirementCalculator.exception.InvalidParameterException;
import com.example.retirementCalculator.exception.LifestyleNotFoundException;
import com.example.retirementCalculator.logic.RetirementCalculatorServiceImpl;
import com.example.retirementCalculator.persistance.entities.LifestyleDepositsEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

//@ExtendWith({SpringExtension.class, MockitoExtension.class})
@SpringBootTest
@ActiveProfiles("test")
class RetirementCalculatorServiceImplTest {

    @Mock
    private LifestyleCacheServiceImpl lifestyleCacheService;

    @InjectMocks
    private RetirementCalculatorServiceImpl calculatorService;

    private LifestyleDepositsEntity simpleLifestyle;
    private LifestyleDepositsEntity fancyLifestyle;
    private Method calculateFutureValueMethod;
    private static final int MONTHS_IN_YEAR = 12;
    
    @BeforeEach
    void setUp() throws Exception {
        // Setup mock data for lifestyles
        simpleLifestyle = new LifestyleDepositsEntity();
        simpleLifestyle.setLifestyleType("simple");
        simpleLifestyle.setMonthlyDeposit(new BigDecimal("2000.00"));
        simpleLifestyle.setDescription("Basic lifestyle with moderate expenses");

        fancyLifestyle = new LifestyleDepositsEntity();
        fancyLifestyle.setLifestyleType("fancy");
        fancyLifestyle.setMonthlyDeposit(new BigDecimal("5000.00"));
        fancyLifestyle.setDescription("Luxury lifestyle with premium expenses");
        
        // Get access to the private calculateFutureValue method
        calculateFutureValueMethod = RetirementCalculatorServiceImpl.class.getDeclaredMethod(
                "calculateFutureValue", BigDecimal.class, double.class, int.class);
        calculateFutureValueMethod.setAccessible(true);
    }

    @Test
    @DisplayName("Should calculate future value correctly with reflection")
    void calculateFutureValueDirectly() throws Exception {
        // Test parameters
        BigDecimal monthlyDeposit = new BigDecimal("1000.00");
        double monthlyRate = 0.05 / MONTHS_IN_YEAR; // 5% annual rate
        int months = MONTHS_IN_YEAR * 10; // 10 years
        
        // Invoke the private method directly using reflection
        BigDecimal result = (BigDecimal) calculateFutureValueMethod.invoke(
                calculatorService, monthlyDeposit, monthlyRate, months);
        
        // Expected value for R1000 monthly at 5% for 10 years is approximately R154,710
        BigDecimal expectedApprox = new BigDecimal("154710");
        
        // Set the same scale for comparison
        BigDecimal resultRounded = result.setScale(2, RoundingMode.HALF_UP);
        
        // Check if the result is within 1% of expected value
        BigDecimal diff = expectedApprox.subtract(resultRounded).abs();
        BigDecimal tolerance = expectedApprox.multiply(new BigDecimal("0.01"));
        
        assertTrue(diff.compareTo(tolerance) < 0, 
                "Expected approximately " + expectedApprox + " but got " + resultRounded);
    }
    
    @Test
    @DisplayName("Should calculate future value correctly with zero interest rate")
    void calculateFutureValueWithZeroInterest() throws Exception {
        // Test parameters
        BigDecimal monthlyDeposit = new BigDecimal("1000.00");
        double monthlyRate = 0.0;
        int months = MONTHS_IN_YEAR * 5; // 5 years
        
        // For zero interest, we need to mock the handling approach
        // Since division by zero would occur in the original formula
        when(lifestyleCacheService.getLifestyleByType("simple")).thenReturn(Optional.of(simpleLifestyle));
        
        // Create a request with zero interest
        RetirementCalculatorRequestDTO request = RetirementCalculatorRequestDTO.builder()
                .currentAge(30)
                .retirementAge(35)
                .interestRate(0.0)
                .lifestyleType("simple")
                .build();
        
        // Call the public method that would use calculateFutureValue internally
        RetirementCalculatorResponseDTO response = calculatorService.calculateRetirementSavings(request);
        
        // Expected value for R2000 monthly at 0% for 5 years is exactly R120,000
        BigDecimal expected = new BigDecimal("120000.00").setScale(2, RoundingMode.HALF_UP);
        
        assertEquals(expected, response.getTotalRetirementSavings());
    }
    
    @Test
    @DisplayName("Should validate and throw exception for retirement age <= current age")
    void validateParametersWithInvalidAges() {
        // Arrange - retirement age less than current age
        RetirementCalculatorRequestDTO request = RetirementCalculatorRequestDTO.builder()
                .currentAge(65)
                .retirementAge(60)
                .interestRate(5.0)
                .lifestyleType("simple")
                .build();

        // Act & Assert
        InvalidParameterException exception = assertThrows(InvalidParameterException.class, () -> 
            calculatorService.calculateRetirementSavings(request)
        );
        
        assertEquals("Retirement age must be greater than current age", exception.getMessage());
        
        // Arrange - retirement age equal to current age
        RetirementCalculatorRequestDTO request2 = RetirementCalculatorRequestDTO.builder()
                .currentAge(65)
                .retirementAge(65)
                .interestRate(5.0)
                .lifestyleType("simple")
                .build();

        // Act & Assert
        InvalidParameterException exception2 = assertThrows(InvalidParameterException.class, () -> 
            calculatorService.calculateRetirementSavings(request2)
        );
        
        assertEquals("Retirement age must be greater than current age", exception2.getMessage());
    }
    
    @Test
    @DisplayName("Should validate and throw exception for negative interest rate")
    void validateParametersWithNegativeInterestRate() {
        // Arrange
        RetirementCalculatorRequestDTO request = RetirementCalculatorRequestDTO.builder()
                .currentAge(30)
                .retirementAge(65)
                .interestRate(-2.5)
                .lifestyleType("simple")
                .build();

        // Act & Assert
        InvalidParameterException exception = assertThrows(InvalidParameterException.class, () -> 
            calculatorService.calculateRetirementSavings(request)
        );
        
        assertEquals("Interest rate cannot be negative", exception.getMessage());
    }
    
    @Test
    @DisplayName("Should validate and throw exception for years to retirement > 100")
    void validateParametersWithExcessiveYearsToRetirement() {
        // Arrange
        RetirementCalculatorRequestDTO request = RetirementCalculatorRequestDTO.builder()
                .currentAge(30)
                .retirementAge(131)  // 101 years difference
                .interestRate(5.0)
                .lifestyleType("simple")
                .build();

        // Act & Assert
        InvalidParameterException exception = assertThrows(InvalidParameterException.class, () -> 
            calculatorService.calculateRetirementSavings(request)
        );
        
        assertEquals("Years to retirement cannot exceed 100", exception.getMessage());
    }
    
    @Test
    @DisplayName("Should throw LifestyleNotFoundException when lifestyle type not found")
    void calculateRetirementSavingsWithNonExistentLifestyle() {
        // Arrange
        RetirementCalculatorRequestDTO request = RetirementCalculatorRequestDTO.builder()
                .currentAge(30)
                .retirementAge(65)
                .interestRate(5.0)
                .lifestyleType("nonexistent")
                .build();

        when(lifestyleCacheService.getLifestyleByType("nonexistent")).thenReturn(Optional.empty());

        // Act & Assert
        LifestyleNotFoundException exception = assertThrows(LifestyleNotFoundException.class, () -> 
            calculatorService.calculateRetirementSavings(request)
        );
        
        assertEquals("Lifestyle type not found: " + request.getLifestyleType(), exception.getMessage());
    }
    
    @Test
    @DisplayName("Should calculate future value correctly for different time periods")
    void calculateFutureValueForDifferentPeriods() throws Exception {
        BigDecimal monthlyDeposit = new BigDecimal("1000.00");
        double monthlyRate = 0.06 / MONTHS_IN_YEAR; // 6% annual rate
        
        // Test case 1: 1 year
        int months1 = MONTHS_IN_YEAR;
        BigDecimal result1 = (BigDecimal) calculateFutureValueMethod.invoke(
                calculatorService, monthlyDeposit, monthlyRate, months1);
        BigDecimal expected1 = new BigDecimal("12335.56").setScale(2, RoundingMode.HALF_UP);
        BigDecimal actual1 = result1.setScale(2, RoundingMode.HALF_UP);
        assertEquals(expected1, actual1,
                "1 year expected ~ " + expected1 + " but got " + actual1);

        // Test case 2: 5 years
        int months2 = MONTHS_IN_YEAR * 5;
        BigDecimal result2 = (BigDecimal) calculateFutureValueMethod.invoke(
                calculatorService, monthlyDeposit, monthlyRate, months2);
        BigDecimal expected2 = new BigDecimal("69770.03").setScale(2, RoundingMode.HALF_UP);
        BigDecimal actual2 = result2.setScale(2, RoundingMode.HALF_UP);
        assertEquals(expected2, actual2,
                "5 years expected ~ " + expected2 + " but got " + actual2);
        
        // Test case 3: 20 years 
        int months3 = MONTHS_IN_YEAR * 20;
        BigDecimal result3 = (BigDecimal) calculateFutureValueMethod.invoke(
                calculatorService, monthlyDeposit, monthlyRate, months3);
        BigDecimal expected3 = new BigDecimal("462040.90").setScale(2, RoundingMode.HALF_UP);
        BigDecimal actual3 = result3.setScale(2, RoundingMode.HALF_UP);
        assertEquals(expected3, actual3,
                "20 years expected ~ " + expected3 + " but got " + actual3);
    }
    
    @Test
    @DisplayName("Should calculate future value correctly for different interest rates")
    void calculateFutureValueForDifferentRates() throws Exception {
        BigDecimal monthlyDeposit = new BigDecimal("1000.00");
        int months = MONTHS_IN_YEAR * 10; // 10 years
        
        // Test case 1: 3% annual rate
        double monthlyRate1 = 0.03 / MONTHS_IN_YEAR;
        BigDecimal result1 = (BigDecimal) calculateFutureValueMethod.invoke(
                calculatorService, monthlyDeposit, monthlyRate1, months);
        BigDecimal expected1 = new BigDecimal("139741.42").setScale(2, RoundingMode.HALF_UP);
        BigDecimal actual1 = result1.setScale(2, RoundingMode.HALF_UP);
        // assert true flavour of assertEquals just to show the difference
        assertTrue(actual1.compareTo(expected1) == 0,
                "3% rate expected ~ " + expected1 + " but got " + actual1);
        
        // Test case 2: 7% annual rate
        double monthlyRate2 = 0.07 / MONTHS_IN_YEAR;
        BigDecimal result2 = (BigDecimal) calculateFutureValueMethod.invoke(
                calculatorService, monthlyDeposit, monthlyRate2, months);
        BigDecimal expected2 = new BigDecimal("173084.81").setScale(2, RoundingMode.HALF_UP);
        BigDecimal actual2 = result2.setScale(2, RoundingMode.HALF_UP);
        assertEquals(expected2, actual2,
                "7% rate expected ~ " + expected2 + " but got " + actual2);
        
        // Test case 3: 12% annual rate
        double monthlyRate3 = 0.12 / MONTHS_IN_YEAR;
        BigDecimal result3 = (BigDecimal) calculateFutureValueMethod.invoke(
                calculatorService, monthlyDeposit, monthlyRate3, months);
        BigDecimal expected3 = new BigDecimal("230038.69").setScale(2, RoundingMode.HALF_UP);
        BigDecimal actual3 = result3.setScale(2, RoundingMode.HALF_UP);
        assertEquals(expected3, actual3,
                "12% rate expected ~ " + expected3 + " but got " + actual3);
    }
    
    @Test
    @DisplayName("Should calculate future value correctly with very small interest rate")
    void calculateFutureValueWithSmallInterestRate() throws Exception {
        BigDecimal monthlyDeposit = new BigDecimal("1000.00");
        double monthlyRate = 0.0001 / MONTHS_IN_YEAR; // 0.01% annual rate
        int months = MONTHS_IN_YEAR * 5; // 5 years
        
        BigDecimal result = (BigDecimal) calculateFutureValueMethod.invoke(
                calculatorService, monthlyDeposit, monthlyRate, months);
        
        // With very small interest, result should be very close to just sum of deposits
        BigDecimal expected = new BigDecimal("60000").setScale(2, RoundingMode.HALF_UP);
        BigDecimal actual = result.setScale(2, RoundingMode.HALF_UP);
        
        BigDecimal diff = expected.subtract(actual).abs();
        BigDecimal tolerance = expected.multiply(new BigDecimal("0.001")); // 0.1% tolerance
        
        assertTrue(diff.compareTo(tolerance) < 0, 
                "With tiny interest, expected close to " + expected + " but got " + actual);
    }
}