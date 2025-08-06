package com.tripsok_back.config;

import java.util.Properties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class EmailConfig {
	private final EmailProperties emailProperties;

	@Bean
	public JavaMailSender getJavaMailSender() {
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		mailSender.setHost(emailProperties.getHost());
		mailSender.setPort(emailProperties.getPort());

		mailSender.setUsername(emailProperties.getUsername());
		mailSender.setPassword(emailProperties.getPassword());

		Properties props = mailSender.getJavaMailProperties();
		props.put("mail.smtp.auth", emailProperties.getAuth());
		props.put("mail.smtp.starttls.enable", emailProperties.getStarttls());
		props.put("mail.debug", emailProperties.getDebug());
		props.put("mail.smtp.connectiontimeout", emailProperties.getConnectionTimeout());

		return mailSender;
	}
}
