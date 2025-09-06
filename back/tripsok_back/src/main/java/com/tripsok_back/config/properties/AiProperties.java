package com.tripsok_back.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@ConfigurationProperties(prefix = "client.ai")
@Getter
@Setter
public class AiProperties {
	private String serviceKey;
}
