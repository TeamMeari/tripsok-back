package com.tripsok_back.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@ConfigurationProperties(prefix = "client.jwt")
@Getter
@Setter
public class JwtProperties {
	private String secretKey;
	private long accessTokenExpirationTime;
	private long refreshTokenExpirationTime;
	private long emailVerificationTokenExpirationTime;
	private long oauth2AccessTokenExpirationTime;
}
