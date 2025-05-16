package com.example.retirementCalculator.cache;

import com.example.retirementCalculator.exception.CacheException;
import com.example.retirementCalculator.persistance.entities.LifestyleDepositsEntity;
import com.example.retirementCalculator.persistance.repositories.LifestyleDepositsRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LifestyleCacheServiceImplTest {

    private RedisTemplate<String, Object> redisTemplate;
    private ValueOperations<String, Object> valueOps;
    private LifestyleDepositsRepo lifestyleRepo;

    private LifestyleCacheServiceImpl cacheService;

    private LifestyleDepositsEntity simpleLifestyle;
    private LifestyleDepositsEntity fancyLifestyle;

    @BeforeEach
    void setUp() {
        redisTemplate = mock(RedisTemplate.class);
        valueOps = mock(ValueOperations.class);
        lifestyleRepo = mock(LifestyleDepositsRepo.class);

        when(redisTemplate.opsForValue()).thenReturn(valueOps);

        cacheService = new LifestyleCacheServiceImpl(redisTemplate, lifestyleRepo);
        cacheService.init(); // Calls initializeCache as well

        simpleLifestyle = new LifestyleDepositsEntity();
        simpleLifestyle.setLifestyleType("simple");
        simpleLifestyle.setMonthlyDeposit(BigDecimal.valueOf(2000.00));
        simpleLifestyle.setDescription("Basic lifestyle with moderate expenses");

        fancyLifestyle = new LifestyleDepositsEntity();
        fancyLifestyle.setLifestyleType("fancy");
        fancyLifestyle.setMonthlyDeposit(BigDecimal.valueOf(5000.00));
        fancyLifestyle.setDescription("Luxury lifestyle with premium expenses");
    }

    @Test
    void testGetLifestyleByType_CacheHit() {
        when(valueOps.get("lifestyle:simple")).thenReturn(simpleLifestyle);

        Optional<LifestyleDepositsEntity> result = cacheService.getLifestyleByType("simple");

        assertTrue(result.isPresent());
        assertEquals("simple", result.get().getLifestyleType());
        verify(valueOps, never()).set(anyString(), any(), anyLong(), any());
    }

    @Test
    void testGetLifestyleByType_CacheMissThenHitDb() {
        when(valueOps.get("lifestyle:fancy")).thenReturn(null);
        when(lifestyleRepo.findByLifestyleTypeIgnoreCase("fancy")).thenReturn(Optional.of(fancyLifestyle));

        Optional<LifestyleDepositsEntity> result = cacheService.getLifestyleByType("fancy");

        assertTrue(result.isPresent());
        verify(valueOps).set(eq("lifestyle:fancy"), eq(fancyLifestyle), eq(24L), eq(TimeUnit.HOURS));
    }

    @Test
    void testGetAllLifestyles_CacheHit() {
        List<LifestyleDepositsEntity> cached = Arrays.asList(simpleLifestyle, fancyLifestyle);
        when(valueOps.get("lifestyle:all")).thenReturn(cached);

        List<LifestyleDepositsEntity> result = cacheService.getAllLifestyles();

        assertEquals(2, result.size());
        assertEquals("simple", result.get(0).getLifestyleType());
    }

    @Test
    void testGetAllLifestyles_CacheMissThenHitDb() {
        when(valueOps.get("lifestyle:all")).thenReturn(null);
        when(lifestyleRepo.findAll()).thenReturn(List.of(fancyLifestyle));

        List<LifestyleDepositsEntity> result = cacheService.getAllLifestyles();

        assertEquals(1, result.size());
        verify(valueOps).set(eq("lifestyle:all"), eq(List.of(fancyLifestyle)), eq(24L), eq(TimeUnit.HOURS));
    }

    @Test
    void testInitializeCache_StoresAllData() {
        when(lifestyleRepo.findAll()).thenReturn(List.of(simpleLifestyle, fancyLifestyle));

        cacheService.initializeCache();

        verify(valueOps).set("lifestyle:simple", simpleLifestyle, 24, TimeUnit.HOURS);
        verify(valueOps).set("lifestyle:fancy", fancyLifestyle, 24, TimeUnit.HOURS);
        verify(valueOps).set("lifestyle:all", List.of(simpleLifestyle, fancyLifestyle), 24, TimeUnit.HOURS);
    }

    @Test
    void testRefreshCache() {
        Set<String> keys = Set.of("lifestyle:simple", "lifestyle:fancy");
        when(redisTemplate.keys("lifestyle:*")).thenReturn(keys);
        when(lifestyleRepo.findAll()).thenReturn(List.of(simpleLifestyle, fancyLifestyle));

        cacheService.refreshCache();

        ArgumentCaptor<Collection<String>> captor = ArgumentCaptor.forClass(Collection.class);
        verify(redisTemplate).delete(captor.capture());

        // Verify contents without caring about order
        assertTrue(captor.getValue().containsAll(keys));
        assertEquals(keys.size(), captor.getValue().size());

        verify(valueOps).set("lifestyle:all", List.of(simpleLifestyle, fancyLifestyle), 24, TimeUnit.HOURS);
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
        when(valueOps.get(anyString())).thenThrow(new RuntimeException("Redis down"));

        CacheException ex = assertThrows(CacheException.class,
                () -> cacheService.getLifestyleByType("simple"));

        assertTrue(ex.getMessage().contains("Failed to retrieve lifestyle data from cache"));
    }
}
