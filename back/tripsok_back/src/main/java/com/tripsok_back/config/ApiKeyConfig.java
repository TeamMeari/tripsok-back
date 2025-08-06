package com.tripsok_back.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Getter;

@Getter
@Component
public class ApiKeyConfig {

	@Value("${TOUR_API_KEY}")
	private String tourApiKey;

}
