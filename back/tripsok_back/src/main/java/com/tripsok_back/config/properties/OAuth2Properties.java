package com.tripsok_back.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@ConfigurationProperties(prefix = "oauth2")
@Setter
@Getter
public class OAuth2Properties {
	private Google google;

	@Getter
	@Setter
	public static class Google {
		private String clientId;
		private String clientSecret;
		private String redirectUri;
		private String tokenUri;
	}
}
