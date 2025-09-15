package com.tripsok_back.config;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.translate.v3.TranslationServiceClient;
import com.google.cloud.translate.v3.TranslationServiceSettings;

@Configuration
public class GcpTranslateConfig {
	@Value("${google.translate.keyJson}")
	private String keyJson;

	@Bean
	public TranslationServiceClient translationServiceClient() throws IOException {
		GoogleCredentials credentials = GoogleCredentials
			.fromStream(new ByteArrayInputStream(keyJson.getBytes(StandardCharsets.UTF_8)));

		return TranslationServiceClient.create(
			TranslationServiceSettings.newBuilder()
				.setCredentialsProvider(FixedCredentialsProvider.create(credentials))
				.build()
		);
	}
}

