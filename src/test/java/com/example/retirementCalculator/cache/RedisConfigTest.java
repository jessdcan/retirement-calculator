package com.example.retirementCalculator.cache;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;

import java.lang.reflect.Field;

public class RedisConfigTest {

    private RedisConfig redisConfig;

    @BeforeEach
    void setUp() throws Exception {
        redisConfig = new RedisConfig();

        // Inject test property values using reflection (or use @TestPropertySource if Spring context is used)
        Field hostField = RedisConfig.class.getDeclaredField("redisHost");
        hostField.setAccessible(true);
        hostField.set(redisConfig, "test-host");

        Field portField = RedisConfig.class.getDeclaredField("redisPort");
        portField.setAccessible(true);
        portField.set(redisConfig, 1234);
    }

    @Test
    void testRedisConnectionFactory() {
        RedisConnectionFactory factory = redisConfig.redisConnectionFactory();
        assertNotNull(factory);
        assertTrue(factory instanceof LettuceConnectionFactory);

        LettuceConnectionFactory lettuceFactory = (LettuceConnectionFactory) factory;
        lettuceFactory.afterPropertiesSet(); // initialize

        RedisStandaloneConfiguration standaloneConfig = lettuceFactory.getStandaloneConfiguration();
        assertNotNull(standaloneConfig);
        assertEquals("test-host", standaloneConfig.getHostName());
        assertEquals(1234, standaloneConfig.getPort());
    }

    @Test
    void testRedisTemplateSerializers() {
        RedisConnectionFactory mockFactory = mock(RedisConnectionFactory.class);
        RedisTemplate<String, Object> template = redisConfig.redisTemplate(mockFactory);

        assertNotNull(template);
        assertEquals(StringRedisSerializer.class, template.getKeySerializer().getClass());
        assertEquals(StringRedisSerializer.class, template.getHashKeySerializer().getClass());
        assertEquals(GenericJackson2JsonRedisSerializer.class, template.getValueSerializer().getClass());
        assertEquals(GenericJackson2JsonRedisSerializer.class, template.getHashValueSerializer().getClass());
    }
}
