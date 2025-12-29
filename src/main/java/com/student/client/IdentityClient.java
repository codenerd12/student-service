package com.student.client;

import java.util.Base64;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import com.student.client.dto.ValidateTokenResponse;
import com.student.config.RestClientEnvConfig;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@AllArgsConstructor
public class IdentityClient {
	
	private final WebClient keyCloakApiWebClient;
	private final RestClientEnvConfig restClientEnvConfig;
	
	
	public Mono<ValidateTokenResponse> validateTokenFromIdentity(String accessToken) {
		RestClientEnvConfig.Identity identityConfig = restClientEnvConfig.getIdentityConfig();
		return Mono.just(identityConfig)
				.doOnNext(identity -> log.debug("Identity server validation of access token"))
				.flatMap(idConfig -> Mono.deferContextual(ctx -> 
				
				keyCloakApiWebClient.method(HttpMethod.POST)
				.uri(restClientEnvConfig.getKeyCloakValidateUri())
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.header(HttpHeaders.AUTHORIZATION, getBasicAuthHeader())
				.body(BodyInserters.fromValue(
						identityConfig.getValidateTokenBody().replace("{access_token}", accessToken)))
				.retrieve()
				.toEntity(ValidateTokenResponse.class)
				.map(objectResponseEntity -> {
					return objectResponseEntity.getBody();
				})
				.doOnError(throwable -> log.error("Error occured while validating token from Identity: {}", throwable))
				));
	}


	private String getBasicAuthHeader() {
		RestClientEnvConfig.Identity identityConfig = restClientEnvConfig.getIdentityConfig();
		String credentials = identityConfig.getClientId() +":"+ identityConfig.getClientSecret();
		return "Basic " + Base64.getEncoder().encodeToString(credentials.getBytes());
	}

}
