//package com.example.retirementCalculator.cache;
//
//import com.example.retirementCalculator.exception.CacheException;
//import com.example.retirementCalculator.exception.LifestyleNotFoundException;
//import com.example.retirementCalculator.persistance.entities.LifestyleDepositsEntity;
//import com.example.retirementCalculator.persistance.repositories.LifestyleDepositsRepo;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.context.TestConfiguration;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Primary;
//import org.springframework.data.redis.connection.RedisConnectionFactory;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.data.redis.core.ValueOperations;
//import org.springframework.test.context.ActiveProfiles;
//
//import java.math.BigDecimal;
//import java.util.Arrays;
//import java.util.List;
//import java.util.Optional;
//import java.util.Set;
//import java.util.concurrent.TimeUnit;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.*;
//import static org.mockito.Mockito.*;
//
//@SpringBootTest
//@ActiveProfiles("test")
//public class LifestyleCacheServiceImplTest {
//
//    @TestConfiguration
//    public class RedisTestConfig {
//
//        @Bean
//        @Primary
//        public RedisConnectionFactory redisConnectionFactory() {
//            return mock(RedisConnectionFactory.class);
//        }
//
//        @Bean
//        @Primary
//        public RedisTemplate<String, Object> redisTemplate() {
//            RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
//            redisTemplate.setConnectionFactory(redisConnectionFactory());
//            return redisTemplate;
//        }
//    }
//
//    @Mock
//    private RedisTemplate<String, Object> redisTemplate;
//
//    @Mock
//    private LifestyleDepositsRepo lifestyleRepository;
//
//    @Mock
//    private ValueOperations<String, Object> valueOperations;
//
//    @InjectMocks
//    private LifestyleCacheServiceImpl lifestyleCacheService;
//
//    private LifestyleDepositsEntity fancyLifestyle;
//    private LifestyleDepositsEntity simpleLifestyle;
//    private List<LifestyleDepositsEntity> allLifestyles;
//
//    @BeforeEach
//    void setUp() {
//        // Setup test data
//        fancyLifestyle = new LifestyleDepositsEntity();
//        fancyLifestyle.setLifestyleType("fancy");
//        fancyLifestyle.setMonthlyDeposit(BigDecimal.valueOf(5000.0));
//
//        simpleLifestyle = new LifestyleDepositsEntity();
//        simpleLifestyle.setLifestyleType("simple");
//        simpleLifestyle.setMonthlyDeposit(BigDecimal.valueOf(2000.0));
//
//        allLifestyles = Arrays.asList(fancyLifestyle, simpleLifestyle);
//
//        // Setup mocks
//        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
//    }
//
//    @Test
//    void initShouldInitializeValueOpsAndCache() {
//        // Arrange
//        when(lifestyleRepository.findAll()).thenReturn(allLifestyles);
//
//        // Act
//        lifestyleCacheService.init();
//
//        // Assert
//        verify(redisTemplate).opsForValue();
//        verify(lifestyleRepository).findAll();
//        verify(valueOperations, times(3)).set(anyString(), any(), anyLong(), any(TimeUnit.class));
//    }
//
//    @Test
//    void getLifestyleByTypeShouldReturnFromCacheWhenAvailable() {
//        // Arrange
//        when(valueOperations.get("lifestyle:fancy")).thenReturn(fancyLifestyle);
//
//        // Act
//        Optional<LifestyleDepositsEntity> result = lifestyleCacheService.getLifestyleByType("fancy");
//
//        // Assert
//        assertTrue(result.isPresent());
//        assertEquals("fancy", result.get().getLifestyleType());
//        verify(valueOperations).get("lifestyle:fancy");
//        verify(lifestyleRepository, never()).findByLifestyleTypeIgnoreCase(anyString());
//    }
//
//    @Test
//    void getLifestyleByTypeShouldFetchFromDatabaseOnCacheMiss() {
//        // Arrange
//        when(valueOperations.get("lifestyle:simple")).thenReturn(null);
//        when(lifestyleRepository.findByLifestyleTypeIgnoreCase("simple")).thenReturn(Optional.of(simpleLifestyle));
//
//        // Act
//        Optional<LifestyleDepositsEntity> result = lifestyleCacheService.getLifestyleByType("simple");
//
//        // Assert
//        assertTrue(result.isPresent());
//        assertEquals("simple", result.get().getLifestyleType());
//        verify(valueOperations).get("lifestyle:simple");
//        verify(lifestyleRepository).findByLifestyleTypeIgnoreCase("simple");
//        verify(valueOperations).set(eq("lifestyle:simple"), eq(simpleLifestyle), eq(24L), eq(TimeUnit.HOURS));
//    }
//
//    @Test
//    void getLifestyleByTypeShouldReturnEmptyWhenNotFound() {
//        // Arrange
//        when(valueOperations.get("lifestyle:budget")).thenReturn(null);
//        when(lifestyleRepository.findByLifestyleTypeIgnoreCase("Budget")).thenReturn(Optional.empty());
//
//        // Act & Assert
//        assertThrows(LifestyleNotFoundException.class, () -> {
//            lifestyleCacheService.getLifestyleByType("Budget");
//        });
//    }
//
//    @Test
//    void getAllLifestylesShouldReturnFromCacheWhenAvailable() {
//        // Arrange
//        when(valueOperations.get("lifestyle:all")).thenReturn(allLifestyles);
//
//        // Act
//        List<LifestyleDepositsEntity> result = lifestyleCacheService.getAllLifestyles();
//
//        // Assert
//        assertEquals(2, result.size());
//        verify(valueOperations).get("lifestyle:all");
//        verify(lifestyleRepository, never()).findAll();
//    }
//
//    @Test
//    void getAllLifestylesShouldFetchFromDatabaseOnCacheMiss() {
//        // Arrange
//        when(valueOperations.get("lifestyle:all")).thenReturn(null);
//        when(lifestyleRepository.findAll()).thenReturn(allLifestyles);
//
//        // Act
//        List<LifestyleDepositsEntity> result = lifestyleCacheService.getAllLifestyles();
//
//        // Assert
//        assertEquals(2, result.size());
//        verify(valueOperations).get("lifestyle:all");
//        verify(lifestyleRepository).findAll();
//        verify(valueOperations).set(eq("lifestyle:all"), eq(allLifestyles), eq(24L), eq(TimeUnit.HOURS));
//    }
//
//    @Test
//    void initializeCacheShouldPopulateCache() {
//        // Arrange
//        when(lifestyleRepository.findAll()).thenReturn(allLifestyles);
//
//        // Act
//        lifestyleCacheService.initializeCache();
//
//        // Assert
//        verify(lifestyleRepository).findAll();
//        verify(valueOperations).set(eq("lifestyle:fancy"), eq(fancyLifestyle), eq(24L), eq(TimeUnit.HOURS));
//        verify(valueOperations).set(eq("lifestyle:simple"), eq(simpleLifestyle), eq(24L), eq(TimeUnit.HOURS));
//        verify(valueOperations).set(eq("lifestyle:all"), eq(allLifestyles), eq(24L), eq(TimeUnit.HOURS));
//    }
//
//    @Test
//    void refreshCacheShouldClearAndReload() {
//        // Arrange
//        Set<String> keys = Set.of("lifestyle:fancy", "lifestyle:simple", "lifestyle:all");
//        when(redisTemplate.keys("lifestyle:*")).thenReturn(keys);
//        when(lifestyleRepository.findAll()).thenReturn(allLifestyles);
//
//        // Act
//        lifestyleCacheService.refreshCache();
//
//        // Assert
//        verify(redisTemplate).keys("lifestyle:*");
//        verify(redisTemplate).delete(anyList());
//        verify(lifestyleRepository).findAll();
//        verify(valueOperations, times(3)).set(anyString(), any(), eq(24L), eq(TimeUnit.HOURS));
//    }
//
//    @Test
//    void isCacheHealthyShouldReturnTrueWhenAllKeysExist() {
//        // Arrange
//        when(redisTemplate.hasKey("lifestyle:all")).thenReturn(true);
//
//        // Act
//        boolean result = lifestyleCacheService.isCacheHealthy();
//
//        // Assert
//        assertTrue(result);
//        verify(redisTemplate).hasKey("lifestyle:all");
//    }
//
//    @Test
//    void isCacheHealthyShouldReturnFalseWhenExceptionOccurs() {
//        // Arrange
//        when(redisTemplate.hasKey(anyString())).thenThrow(new RuntimeException("Redis connection failed"));
//
//        // Act
//        boolean result = lifestyleCacheService.isCacheHealthy();
//
//        // Assert
//        assertFalse(result);
//    }
//
//    @Test
//    void initializeCacheShouldHandleEmptyRepositoryResult() {
//        // Arrange
//        when(lifestyleRepository.findAll()).thenReturn(List.of());
//
//        // Act
//        lifestyleCacheService.initializeCache();
//
//        // Assert
//        verify(lifestyleRepository).findAll();
//        verify(valueOperations, never()).set(anyString(), any(), anyLong(), any(TimeUnit.class));
//    }
//
//    @Test
//    void initializeCacheShouldPropagateExceptions() {
//        // Arrange
//        when(lifestyleRepository.findAll()).thenThrow(new RuntimeException("Database error"));
//
//        // Act & Assert
//        assertThrows(CacheException.class, () -> lifestyleCacheService.initializeCache());
//    }
//}