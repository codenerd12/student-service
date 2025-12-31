package com.student.cache;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.Cache;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class InMemoryCacheService<V> implements CacheService<V> {
	
	private final Cache<String, Object> caffeineCache;
	private final ObjectMapper objectMapper;

	@Override
	public Mono<V> get(String key) {
		V value = (V) caffeineCache.getIfPresent(key);
		return Mono.justOrEmpty(value);
	}

	@Override
	public Mono<Void> put(String key, V value) {
		int oneHourAfterFMEnablement = 1;
		LocalDateTime startOfTomorrow = LocalDate.now().plusDays(1).atStartOfDay();
		Duration duration = Duration.between(LocalDateTime.now(), startOfTomorrow).plusHours(oneHourAfterFMEnablement);
		return Mono.fromRunnable(() -> {
			caffeineCache.put(key, value);
			caffeineCache.policy().expireAfterWrite().ifPresent(expire -> expire.setExpiresAfter(duration));
		});
	}

	@Override
	public Mono<Void> put(String key, V value, Duration ttl) {
		return Mono.fromRunnable(() -> {
			caffeineCache.put(key, value);
			caffeineCache.policy().expireAfterWrite().ifPresent(expire -> expire.setExpiresAfter(ttl));
		});
	}

	@Override
	public Mono<Void> evictAll(String cacheName) {
		return Mono.fromRunnable(caffeineCache::invalidateAll);
	}

}
