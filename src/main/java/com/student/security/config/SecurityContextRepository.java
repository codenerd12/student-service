package com.student.security.config;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.student.client.dto.ValidateTokenResponse;
import com.student.exceptions.UnAuthorizedException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class SecurityContextRepository implements ServerSecurityContextRepository {
	
	private final ServerAuthenticationManager userAuthenticationManager;

	@Override
	public Mono<Void> save(ServerWebExchange exchange, SecurityContext context) {
		return null;
	}

	@Override
	public Mono<SecurityContext> load(ServerWebExchange exchange) {
		return Mono.justOrEmpty(exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION))
				.filter(accessToken -> accessToken.startsWith("Bearer "))
				.doOnNext(accessToken -> log.debug("Access Token Found in Header: {}",accessToken))
				.switchIfEmpty(Mono.error(new UnAuthorizedException(
						HttpStatus.UNAUTHORIZED, "Missing Access Token. Access Denied")))
				.map(accessToken -> accessToken.replace("Bearer ", ""))
				.map(userAccessToken -> new PreAuthenticatedAuthenticationToken(userAccessToken, ValidateTokenResponse.builder().build()))
				.flatMap(userAuthenticationManager::authenticate)
				.doOnNext(authentication -> log.debug("Request Authenticated status={},", authentication.isAuthenticated()))
				.map(SecurityContextImpl::new);
	}

}
