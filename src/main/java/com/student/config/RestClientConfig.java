package com.student.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

import io.opentelemetry.api.trace.Span;


@Configuration
public class RestClientConfig {
	
	private final RestClientEnvConfig restClientEnvConfig;
	
	public RestClientConfig(RestClientEnvConfig restClientEnvConfig) {
		this.restClientEnvConfig = restClientEnvConfig;
	}
	
	@Bean
	public WebClient.Builder webClientBuilder() {
		return WebClient.builder()
				.codecs(config -> config.defaultCodecs()
						.maxInMemorySize(8 * 1024 * 1024))
				.defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
				.filter(traceContextFilter());
	}
	
	@Bean
	public WebClient keyCloakApiWebClient(WebClient.Builder webClientBuilder) {
		return webClientBuilder.baseUrl(restClientEnvConfig.getKeyCloakBaseUrl()).build();
	}

	private ExchangeFilterFunction traceContextFilter() {
		return (request, next) -> {
			var spanContext = Span.current().getSpanContext();
			//var traceparent = 
			var clientRequest = ClientRequest.from(request).headers(httpHeaders -> {
				httpHeaders.add("traceparent", "abc");
				httpHeaders.add("traceid", "123");
				httpHeaders.add("teamnumber", "307");
			}).build();
			
			return next.exchange(clientRequest);
		};
	}

}
