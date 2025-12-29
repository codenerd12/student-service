package com.student.security.config;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.web.server.ServerWebExchange;

import com.student.exceptions.UnAuthorizedException;

import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;

@AllArgsConstructor
public class UserAccessDeniedHandler implements ServerAccessDeniedHandler {
	
	public UserAccessDeniedHandler(HttpStatus httpStatus) {}

	@Override
	public Mono<Void> handle(ServerWebExchange exchange, AccessDeniedException denied) {
		// TODO Auto-generated method stub
		return Mono.error(new UnAuthorizedException(HttpStatus.UNAUTHORIZED, "Token is Not-Active"));
	}

}
