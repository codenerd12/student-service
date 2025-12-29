package com.student.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@NoArgsConstructor
public class ValidateTokenResponse {
	
	@JsonProperty("sub")
	private String subject;
	
	@JsonProperty("confidence_level")
	private String confidenceLevel;
	
	@JsonProperty("client_id")
	private String clientId;
	
	@JsonProperty("token_type")
	private String tokenType;
	
	@JsonProperty("active")
	private String active;
	

}
