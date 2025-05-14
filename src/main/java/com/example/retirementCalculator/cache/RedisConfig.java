package com.example.retirementCalculator.cache;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import lombok.extern.slf4j.Slf4j;

/**
 * Configuration class for Redis connection and serialization settings.
 * <p>
 * Sets up the Redis connection factory and template with appropriate serializers.
 * </p>
 *
 * @author Your Name
 * @version 1.0
 * @since 1.0
 */
@Configuration
@Slf4j
public class RedisConfig {

    @Value("${spring.data.redis.host:localhost}")
    private String redisHost;

    @Value("${spring.data.redis.port:6379}")
    private int redisPort;

    /**
     * Creates a Redis connection factory using Lettuce.
     * <p>
     * Configures the connection to the Redis server using host and port
     * values from application properties.
     * </p>
     *
     * @return Configured Redis connection factory
     */
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration(redisHost, redisPort);
        log.info("Configuring Redis connection to {}:{}", redisHost, redisPort);
        return new LettuceConnectionFactory(redisConfig);
    }

    /**
     * Creates a Redis template with appropriate serializers.
     * <p>
     * Configures the template with string key serializer and JSON value serializer
     * for flexible object storage and retrieval.
     * </p>
     *
     * @param connectionFactory The Redis connection factory
     * @return Configured Redis template
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // Use StringRedisSerializer for keys
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());

        // Use Jackson serializer for values
        GenericJackson2JsonRedisSerializer jacksonSerializer = new GenericJackson2JsonRedisSerializer();
        template.setValueSerializer(jacksonSerializer);
        template.setHashValueSerializer(jacksonSerializer);

        log.info("Configured RedisTemplate with StringRedisSerializer for keys and GenericJackson2JsonRedisSerializer for values");
        return template;
    }
}