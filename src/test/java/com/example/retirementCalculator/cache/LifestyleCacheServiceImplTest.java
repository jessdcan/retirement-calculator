package com.example.retirementCalculator.cache;

import com.example.retirementCalculator.exception.CacheException;
import com.example.retirementCalculator.persistance.entities.LifestyleDepositsEntity;
import com.example.retirementCalculator.persistance.repositories.LifestyleDepositsRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
class LifestyleCacheServiceImplTest {

    @MockBean
    private RedisTemplate<String, Object> redisTemplate;

    @MockBean
    private ValueOperations<String, Object> valueOperations;

    @Autowired
    private LifestyleCacheService cacheService;

    @Autowired
    private LifestyleDepositsRepo lifestyleRepo;

    private LifestyleDepositsEntity simpleLifestyle;
    private LifestyleDepositsEntity fancyLifestyle;

    @BeforeEach
    void setUp() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        // Clear DB
        lifestyleRepo.deleteAll();

//        // Reset mocks
//        reset(redisTemplate);

        // Create test data and save to H2 database
        simpleLifestyle = new LifestyleDepositsEntity();
        simpleLifestyle.setLifestyleType("simple");
        simpleLifestyle.setMonthlyDeposit(BigDecimal.valueOf(2000.00));
        simpleLifestyle.setDescription("Basic lifestyle with moderate expenses");
        lifestyleRepo.save(simpleLifestyle);

        fancyLifestyle = new LifestyleDepositsEntity();
        fancyLifestyle.setLifestyleType("fancy");
        fancyLifestyle.setMonthlyDeposit(BigDecimal.valueOf(5000.00));
        fancyLifestyle.setDescription("Luxury lifestyle with premium expenses");
        lifestyleRepo.save(fancyLifestyle);
    }

    @Test
    void testGetLifestyleByType_CacheHit() {
        when(redisTemplate.opsForValue().get("lifestyle:simple")).thenReturn(simpleLifestyle);

        Optional<LifestyleDepositsEntity> result = cacheService.getLifestyleByType("simple");

        assertTrue(result.isPresent());
        assertEquals("simple", result.get().getLifestyleType());
        // Verify we don't set the cache because we got a cache hit
        verify(redisTemplate.opsForValue()).set(anyString(), any(), anyLong(), any());
    }

    @Test
    void testGetLifestyleByType_CacheMissThenHitDb() {
        when(redisTemplate.opsForValue().get("lifestyle:fancy")).thenReturn(null);

        Optional<LifestyleDepositsEntity> result = cacheService.getLifestyleByType("fancy");

        assertTrue(result.isPresent());
        assertEquals("fancy", result.get().getLifestyleType());
        // Verify we loaded from DB and set the cache
        verify(redisTemplate.opsForValue()).set(eq("lifestyle:fancy"), any(LifestyleDepositsEntity.class), eq(24L), eq(TimeUnit.HOURS));
    }

    @Test
    void testGetAllLifestyles_CacheHit() {
        List<LifestyleDepositsEntity> cached = Arrays.asList(simpleLifestyle, fancyLifestyle);
        when(redisTemplate.opsForValue().get("lifestyle:all")).thenReturn(cached);

        List<LifestyleDepositsEntity> result = cacheService.getAllLifestyles();

        assertEquals(2, result.size());
        // Verify we didn't need to hit the database
        verify(redisTemplate.opsForValue(), never()).set(eq("lifestyle:all"), any(), anyLong(), any());
    }

    @Test
    void testGetAllLifestyles_CacheMissThenHitDb() {
        when(redisTemplate.opsForValue().get("lifestyle:all")).thenReturn(null);

        List<LifestyleDepositsEntity> result = cacheService.getAllLifestyles();

        assertEquals(2, result.size());
        verify(redisTemplate.opsForValue()).set(eq("lifestyle:all"), any(), eq(24L), eq(TimeUnit.HOURS));
    }

    @Test
    void testInitializeCache_StoresAllData() {
        cacheService.initializeCache();

        // Verify data is loaded from H2 and stored in Redis
        verify(redisTemplate.opsForValue()).set(eq("lifestyle:all"), any(), eq(24L), eq(TimeUnit.HOURS));

        // Verify individual lifestyles are cached
        verify(redisTemplate.opsForValue()).set(eq("lifestyle:simple"), any(LifestyleDepositsEntity.class), eq(24L), eq(TimeUnit.HOURS));
        verify(redisTemplate.opsForValue()).set(eq("lifestyle:fancy"), any(LifestyleDepositsEntity.class), eq(24L), eq(TimeUnit.HOURS));
    }

    @Test
    void testRefreshCache() {
        Set<String> keys = Set.of("lifestyle:simple", "lifestyle:fancy", "lifestyle:all");
        when(redisTemplate.keys("lifestyle:*")).thenReturn(keys);

        cacheService.refreshCache();

        // Verify keys were deleted
        verify(redisTemplate).delete(keys);

        // Verify data reloaded from H2 and stored in Redis
        verify(redisTemplate.opsForValue()).set(eq("lifestyle:all"), any(), eq(24L), eq(TimeUnit.HOURS));
        verify(redisTemplate.opsForValue()).set(eq("lifestyle:simple"), any(LifestyleDepositsEntity.class), eq(24L), eq(TimeUnit.HOURS));
        verify(redisTemplate.opsForValue()).set(eq("lifestyle:fancy"), any(LifestyleDepositsEntity.class), eq(24L), eq(TimeUnit.HOURS));
    }

    @Test
    void testIsCacheHealthy_True() {
        when(redisTemplate.hasKey("lifestyle:all")).thenReturn(true);
        assertTrue(cacheService.isCacheHealthy());
    }

    @Test
    void testIsCacheHealthy_False() {
        when(redisTemplate.hasKey("lifestyle:all")).thenReturn(false);
        assertFalse(cacheService.isCacheHealthy());
    }

    @Test
    void testGetLifestyleByType_ExceptionHandled() {
        when(redisTemplate.opsForValue().get(anyString())).thenThrow(new RuntimeException("Redis down"));

        CacheException ex = assertThrows(CacheException.class,
                () -> cacheService.getLifestyleByType("simple"));

        assertTrue(ex.getMessage().contains("Failed to retrieve lifestyle data from cache"));
    }
}