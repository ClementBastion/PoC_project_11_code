package com.medhead.emergency.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class CacheConfig {

    /**
     * Configures the cache manager using Caffeine.
     * This cache manager defines named caches for various application use cases
     * with a maximum size and time-to-live expiration policy.
     *
     * @return the configured Caffeine-based CacheManager
     */
    @Bean
    public CacheManager cacheManager() {
        // Define caches by name: used in @Cacheable annotations
        CaffeineCacheManager cacheManager = new CaffeineCacheManager(
                "allHospitals",
                "findAllWithSpecialities",
                "travelTimes",
                "bestHospital"
        );

        // Configure expiration and size limit
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .expireAfterWrite(5, TimeUnit.MINUTES)  // Expire entries 5 minutes after write
                .maximumSize(1000));                    // Limit cache to 1000 entries

        return cacheManager;
    }
}
