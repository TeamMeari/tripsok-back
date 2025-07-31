package com.tripsok_back.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@ConfigurationProperties(prefix = "client.jwt")
@Getter
@Setter
public class JwtProperties {
	String secretKey;
	long accessTokenExpirationTime;
	long refreshTokenExpirationTime;
	long emailVerificationTokenExpirationTime;
	long oauth2AccessTokenExpirationTime;
}
