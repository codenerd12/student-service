package com.student.security.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Component;

import com.student.client.dto.ValidateTokenResponse;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class ColleagueSecurityContextHolder {
	
	public Mono<String> getColleagueToken() {
		return ReactiveSecurityContextHolder.getContext()
				.map(SecurityContext::getAuthentication)
				.map(Authentication::getPrincipal)
				.map(String.class::cast);
	}
	
	public Mono<String> getColleagueUUID() {
		return ReactiveSecurityContextHolder.getContext()
				.map(SecurityContext::getAuthentication)
				.map(Authentication::getCredentials)
				.map(ValidateTokenResponse.class::cast)
				.map(ValidateTokenResponse::getSubject);
	}

}
