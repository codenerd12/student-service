package com.student.config;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.student.cache.CacheService;
import com.student.cache.InMemoryCacheService;
import com.student.cache.RedisCacheService;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class CacheServiceConfig {
	
	public static final String REDIS = "redis";
	
	@Value("${cache.type:caffeine}")
	private String cacheType;
	
	@Value("${cache-duration.minutes}")
	private Integer defaultCacheDuration;
	
	@Bean
	@Primary
	public <T> CacheService<T> cacheService(InMemoryCacheService<T> caffeineCacheService, 
			RedisCacheService<T> redisCacheService) {
		log.info("Using {} cache.", cacheType);
		
		if(REDIS.equalsIgnoreCase(cacheType))
			return redisCacheService;
		
		return caffeineCacheService;
	}
	
	@Bean
	public Cache<String, Object> caffeineCache() {
		return Caffeine.newBuilder()
				.expireAfterWrite(Duration.ofHours(defaultCacheDuration.longValue()))
				.build();
	}

}
