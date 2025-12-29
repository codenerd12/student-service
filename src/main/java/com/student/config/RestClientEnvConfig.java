package com.student.config;

import java.util.Objects;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Configuration
@Getter
@Setter
public class RestClientEnvConfig {

	@Value("${keycloak.base-url}")
	private String keyCloakBaseUrl;
	
	@Value("${keycloak.token-uri}")
	private String keyCloakTokenUri;
	
	@Value("${keycloak.validate-uri}")
	private String keyCloakValidateUri;
	
	@Value("${identity.clientId}")
	private String identityClientId;
	@Value("${identity.clientSecret}")
	private String identityClientSecret;
	@Value("${identity.request-body}")
	private String identityRequestBody;
	@Value("${identity.validate-body}")
	private String identityValidateBody;
	@Value("${identity.valid-clients}")
	private String validClients;
	
	@Data
	@Builder
	public static class Identity {
		private String clientId;
		private String clientSecret;
		private String requestBody;
		private String validateTokenBody;
	}
	
	private Identity identityConfig;
	
	public Identity getIdentityConfig() {
		if(Objects.isNull(identityConfig)) {
			identityConfig = Identity.builder()
					.clientId(identityClientId)
					.clientSecret(identityClientSecret)
					.requestBody(identityRequestBody)
					.validateTokenBody(identityValidateBody)
					.build();
		}
		return identityConfig;
	}
	
	private Set<String> whiteListedClients;
	
	public Set<String> getWhiteListedClients() {
		if(CollectionUtils.isEmpty(whiteListedClients)) {
			String[] clientIds = validClients.split(",");
			this.whiteListedClients = Set.of(clientIds);
		}
		
		return whiteListedClients;
	}

}
