package com.student.security.config;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.student.client.IdentityClient;
import com.student.client.dto.ValidateTokenResponse;
import com.student.config.RestClientEnvConfig;
import com.student.exceptions.UnAuthorizedException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class ServerAuthenticationManager implements ReactiveAuthenticationManager {
	
	private final IdentityClient identityClient;
	private final RestClientEnvConfig restClientEnvConfig;

	@Override
	public Mono<Authentication> authenticate(Authentication authentication) {
		final String userAccessToken = (String) authentication.getPrincipal();
		System.out.println("Token:\n"+userAccessToken);
		return resolveValidationStrategy(userAccessToken)
				.flatMap(this::verifyActive)
				.flatMap(this::verfiyWhitelisted)
				.map(validationTokenResponse -> {
					authentication.setAuthenticated(true);
					ValidateTokenResponse credentials = (ValidateTokenResponse) authentication.getCredentials();
					credentials.setSubject(validationTokenResponse.getSubject());
					return authentication;
				});
	}

	private Mono<ValidateTokenResponse> resolveValidationStrategy(String userAccessToken) {
		return identityClient.validateTokenFromIdentity(userAccessToken);
				
	}
	
	private Mono<ValidateTokenResponse> verifyActive(ValidateTokenResponse tokenResponse) {
		if(!StringUtils.isEmpty(tokenResponse.getActive()) && Boolean.TRUE.toString().equals(tokenResponse.getActive())) {
			return Mono.just(tokenResponse);
		} else {
			return Mono.error(new UnAuthorizedException(HttpStatus.UNAUTHORIZED, "Token is Not Active, Access Denied"));
		}
	}
	
	private Mono<ValidateTokenResponse> verfiyWhitelisted(ValidateTokenResponse tokenResponse) {
		if(restClientEnvConfig.getWhiteListedClients().contains(tokenResponse.getClientId()))
			return Mono.just(tokenResponse);
		else
			return Mono.error(new UnAuthorizedException(HttpStatus.UNAUTHORIZED, "Not Whitelisted Client-id, Access Denied"));
	}

}
