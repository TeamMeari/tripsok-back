package com.tripsok_back.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@ConfigurationProperties(prefix = "client.ai")
@Getter
@Setter
public class AccidentCaseProperties {
String apiKey;
String embeddingKey;
String embeddingUrl;
}
