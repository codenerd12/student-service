package com.student.exceptions;

import org.springframework.http.HttpStatusCode;

import lombok.Data;
@Data
public class UnAuthorizedException extends RuntimeException {
	private HttpStatusCode status;
	
	public UnAuthorizedException(HttpStatusCode status, String message) {
		super(message);
		this.status = status;
	}
	

}
