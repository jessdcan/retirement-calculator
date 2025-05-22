package com.example.retirementCalculator.cache;

import com.example.retirementCalculator.exception.CacheException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InterestRateCacheServiceImplTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @InjectMocks
    private InterestRateCacheServiceImpl interestRateCacheService;

    @BeforeEach
    void setUp() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        ReflectionTestUtils.setField(interestRateCacheService, "valueOps", valueOperations);
    }

    @Test
    void getInterestRateByLifestyleType_ShouldReturnCachedRate() {
        // Arrange
        String lifestyleType = "fancy";
        BigDecimal expectedRate = new BigDecimal("5.5");
        when(valueOperations.get("interest_rate:" + lifestyleType.toLowerCase()))
                .thenReturn(expectedRate);

        // Act
        Optional<BigDecimal> result = interestRateCacheService.getInterestRateByLifestyleType(lifestyleType);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(expectedRate, result.get());
        verify(valueOperations).get("interest_rate:" + lifestyleType.toLowerCase());
    }

    @Test
    void getInterestRateByLifestyleType_ShouldReturnEmptyOnCacheMiss() {
        // Arrange
        String lifestyleType = "unknown";
        when(valueOperations.get("interest_rate:" + lifestyleType.toLowerCase()))
                .thenReturn(null);

        // Act
        Optional<BigDecimal> result = interestRateCacheService.getInterestRateByLifestyleType(lifestyleType);

        // Assert
        assertTrue(result.isEmpty());
        verify(valueOperations).get("interest_rate:" + lifestyleType.toLowerCase());
    }

    @Test
    void getInterestRateByLifestyleType_ShouldThrowExceptionOnError() {
        // Arrange
        String lifestyleType = "fancy";
        when(valueOperations.get(anyString()))
                .thenThrow(new RuntimeException("Redis error"));

        // Act & Assert
        assertThrows(CacheException.class, () -> 
            interestRateCacheService.getInterestRateByLifestyleType(lifestyleType));
    }

    @Test
    void initializeCache_ShouldLoadDataFromCsv() {
        // Arrange
        Set<String> keys = Set.of("interest_rate:fancy", "interest_rate:simple", "interest_rate:all");
        when(redisTemplate.keys("interest_rate:*")).thenReturn(keys);

        // Act
        interestRateCacheService.initializeCache();

        // Assert
        verify(valueOperations, atLeastOnce()).set(anyString(), any(), eq(24L), eq(TimeUnit.HOURS));
    }

    @Test
    void refreshCache_ShouldClearAndReload() {
        // Arrange
        Set<String> keys = Set.of("interest_rate:fancy", "interest_rate:simple", "interest_rate:all");
        when(redisTemplate.keys("interest_rate:*")).thenReturn(keys);

        // Act
        interestRateCacheService.refreshCache();

        // Assert
        verify(redisTemplate).keys("interest_rate:*");
        verify(redisTemplate).delete(anyList());
        verify(valueOperations, atLeastOnce()).set(anyString(), any(), eq(24L), eq(TimeUnit.HOURS));
    }

    @Test
    void isCacheHealthy_ShouldReturnTrueWhenCacheHasData() {
        // Arrange
        when(redisTemplate.hasKey("interest_rate:all")).thenReturn(true);

        // Act
        boolean result = interestRateCacheService.isCacheHealthy();

        // Assert
        assertTrue(result);
        verify(redisTemplate).hasKey("interest_rate:all");
    }

    @Test
    void isCacheHealthy_ShouldReturnFalseWhenExceptionOccurs() {
        // Arrange
        when(redisTemplate.hasKey(anyString())).thenThrow(new RuntimeException("Redis error"));

        // Act
        boolean result = interestRateCacheService.isCacheHealthy();

        // Assert
        assertFalse(result);
    }
} 