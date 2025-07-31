package com.tripsok_back.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@ConfigurationProperties(prefix = "spring.mail")
@Getter
@Setter
public class EmailProperties {
	private String host;
	private int port;
	private String username;
	private String password;
	private Boolean auth;
	private Boolean starttls;
	private Boolean debug;
	private int connectiontimeout;
}
