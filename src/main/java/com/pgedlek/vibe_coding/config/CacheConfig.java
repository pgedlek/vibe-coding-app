package com.pgedlek.vibe_coding.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.pgedlek.vibe_coding.weather.dto.WeatherResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

import com.github.benmanes.caffeine.cache.Cache;

@Configuration
public class CacheConfig {

    @Bean
    public Cache<String, WeatherResponse> weatherCache(
            @Value("${weather.cache.maxSize:500}") long maxSize,
            @Value("${weather.cache.ttlMinutes:15}") long ttlMinutes
    ) {
        return Caffeine.newBuilder()
                .maximumSize(maxSize)
                .expireAfterWrite(Duration.ofMinutes(ttlMinutes))
                .recordStats()
                .build();
    }
}

