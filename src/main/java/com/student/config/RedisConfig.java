package com.student.config;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class RedisConfig {
	@Value("${redis.host}")
	private String redisHost;
	@Value("${redis.port}")
	private int redisPort;
	@Value("${redis.password}")
	private String redisPassword;
	@Value("${redis.connectionTimeoutInMin}")
	private Integer redisConnectionTimeoutInMinutes;
	
	@Bean
	public ReactiveRedisConnectionFactory reactiveRedisConnectionFactory() {
		
		log.info("Creating standalone redis config");
		RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(redisHost, redisPort);
		if(!StringUtils.isEmpty(redisPassword)) {
			config.setPassword(RedisPassword.of(redisPassword));
		}
		
		LettuceClientConfiguration.LettuceClientConfigurationBuilder builder = 
				LettuceClientConfiguration.builder()
				.commandTimeout(Duration.ofMinutes(redisConnectionTimeoutInMinutes));
		
		log.info("Creating ssl enabled redis config");
		builder.useSsl();
		
		LettuceConnectionFactory factory = new LettuceConnectionFactory(config, builder.build());
		factory.afterPropertiesSet();
		log.info("Created reactiveRedisConnectionFactory");
		return factory;
	}
	
	@Bean
	public ReactiveRedisTemplate<String, Object> reactiveRedisTemplate(
			ReactiveRedisConnectionFactory factory) {
		RedisSerializationContext<String, Object> context = 
				RedisSerializationContext.<String, Object>newSerializationContext(new StringRedisSerializer())
				.value(new GenericJackson2JsonRedisSerializer())
				.build();
		return new ReactiveRedisTemplate<>(factory, context);
	}
	
	@Bean
	public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
		return RedisCacheManager.builder(redisConnectionFactory)
				.cacheDefaults(RedisCacheConfiguration.defaultCacheConfig())
				.build();
	}
	
	@Bean
	public ReactiveValueOperations<String, Object> valueOperations(
			ReactiveRedisTemplate<String, Object> reactiveRedisTemplate) {
		return reactiveRedisTemplate.opsForValue();
	}

}
