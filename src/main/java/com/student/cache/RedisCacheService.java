package com.student.cache;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.ReactiveValueOperations;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class RedisCacheService<V> implements CacheService<V> {

	private final ReactiveValueOperations<String, Object> valueOperations;
	private final CacheManager cacheManager;
	
	@Override
	public Mono<V> get(String key) {
		return (Mono<V>) valueOperations.get(key)
				.onErrorResume(e -> {
					log.error("Redis connection exception occured while getting key: {}", key, e);
					return Mono.empty();
				});
	}

	@Override
	public Mono<Void> put(String key, V value) {
		int oneHourAfterFMEnablement = 1;
		LocalDateTime startOfTomorrow = LocalDate.now().plusDays(1).atStartOfDay();
		Duration duration = Duration.between(LocalDateTime.now(), startOfTomorrow).plusHours(oneHourAfterFMEnablement);
		return valueOperations.set(key,  value, duration)
				.onErrorResume(e -> {
					log.error("Redis Connection excption occured while putting key {}", key, e);
					return Mono.empty();
				}).then();
	}

	@Override
	public Mono<Void> put(String key, V value, Duration ttl) {
		return valueOperations.set(key,  value, ttl)
				.onErrorResume(e -> {
					log.error("Redis Connection excption occured while putting key {}", key, e);
					return Mono.empty();
				}).then();
	}

	@Override
	public Mono<Void> evictAll(String cacheName) {
		return Mono.fromRunnable(() -> {
			var cache = cacheManager.getCache(cacheName);
			if(cache!=null) {
				cache.clear();
			} else {
				log.error("Cache : {} doesn't exist.", cacheName);
			}
		});
	}

}
