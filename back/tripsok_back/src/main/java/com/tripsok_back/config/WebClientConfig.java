package com.tripsok_back.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

	@Bean(name = "touristApiWebClient")
	public WebClient touristApiWebClient(WebClient.Builder builder) {
		return builder
			.baseUrl("https://apis.data.go.kr/B551011/KorService2")
			.build();
	}

	@Bean
	public WebClient ollamaApiWebClient(WebClient.Builder builder) {
		return builder
			.baseUrl("http://localhost:11434")
			.exchangeStrategies(ExchangeStrategies.builder()
				.codecs(c -> c.defaultCodecs().maxInMemorySize(4 * 1024 * 1024)) // 4MB
				.build())
			.build();
	}

	@Bean(name = "groqApiWebClient")
	public WebClient groqApiWebClient(WebClient.Builder builder, ApiKeyConfig apiKeyConfig) {
		return builder
			.baseUrl("https://api.groq.com/openai/v1")
			.defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKeyConfig.getGroqApiKey())
			.exchangeStrategies(ExchangeStrategies.builder()
				.codecs(c -> c.defaultCodecs().maxInMemorySize(4 * 1024 * 1024))
				.build())
			.build();
	}
}
