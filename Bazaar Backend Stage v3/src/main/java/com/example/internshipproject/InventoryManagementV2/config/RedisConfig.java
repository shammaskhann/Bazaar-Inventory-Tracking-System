package com.example.internshipproject.InventoryManagementV2.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;

@Configuration
@EnableCaching
public class RedisConfig {

    @Bean
    public ObjectMapper objectMapper() {
        return JsonMapper.builder()
                .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS, true)
                .configure(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE, false)
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                .configure(DeserializationFeature.READ_ENUMS_USING_TO_STRING, true)
                .addModule(new JavaTimeModule())
                .findAndAddModules()
                .build();
    }


    @Bean
    public RedisCacheConfiguration redisCacheConfiguration() {
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10))
                .disableCachingNullValues()
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));
    }

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        RedisCacheManager cacheManager = RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(redisCacheConfiguration())
                .build();
        System.out.println("Redis Cache Manager Initialized: " + cacheManager);
        return cacheManager;
    }
}

