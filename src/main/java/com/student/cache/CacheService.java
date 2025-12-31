package com.student.cache;

import java.time.Duration;

import reactor.core.publisher.Mono;

public interface CacheService<V> {
	
	Mono<V> get(String key);
	
	Mono<Void> put(String key, V value);
	
	Mono<Void> put(String key, V value, Duration ttl);
	
	Mono<Void> evictAll(String cacheName);
	

}
