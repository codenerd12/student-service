package com.student.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.HttpStatusServerEntryPoint;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@RequiredArgsConstructor
public class AuthenticationConfig {
	
	private final ServerAuthenticationManager serverAuthenticationManager;
	private final SecurityContextRepository securityContextRepository;
	
	
	@Bean
	public SecurityWebFilterChain securityWebFilterChain(final ServerHttpSecurity httpSecurity) {
		return httpSecurity
				.csrf(ServerHttpSecurity.CsrfSpec::disable)
				.formLogin(ServerHttpSecurity.FormLoginSpec::disable)
				.httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
				.authorizeExchange(authorizeExchangeSpec -> authorizeExchangeSpec
						.pathMatchers("/extra-hours/_healthcheck","/extra-hours/_status","/h2-console/**")
						.permitAll()
						.anyExchange()
						.authenticated())
				.authenticationManager(serverAuthenticationManager)
				.securityContextRepository(securityContextRepository)
				.exceptionHandling(exceptionHandlingSpec -> exceptionHandlingSpec
						.accessDeniedHandler(new UserAccessDeniedHandler(HttpStatus.UNAUTHORIZED))
						.authenticationEntryPoint(new HttpStatusServerEntryPoint(HttpStatus.UNAUTHORIZED)))
				.build();
	}

}
