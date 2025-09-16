package com.tripsok_back.config;

import java.io.InputStream;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PostConstruct;
import lombok.Getter;

@Getter
@Component
public class ApiKeyConfig {

	@Value("${TOUR_API_KEY}")
	private String tourApiKey;

	@Value("${GROQ_API_KEY}")
	private String groqApiKey;

	// Google Cloud Translate configuration
	@Value("${GOOGLE_API_KEY:}")
	private String googleApiKey;

	@Value("${GOOGLE_PROJECT_ID:}")
	private String googleProjectId;

	@Value("${GOOGLE_LOCATION:global}")
	private String googleLocation;

	@Value("${GOOGLE_KEY_JSON}")
	private String googleKeyJson;

	@PostConstruct
	void resolveProjectIdFromKeyIfMissing() {
		if (googleProjectId != null && !googleProjectId.isBlank()) {
			return;
		}
		try {
			ClassPathResource keyRes = new ClassPathResource(googleKeyJson);
			if (!keyRes.exists()) {
				return;
			}
			try (InputStream in = keyRes.getInputStream()) {
				ObjectMapper mapper = new ObjectMapper();
				Map<String, Object> json = mapper.readValue(in, new TypeReference<>() {
				});
				Object pid = json.get("project_id");
				if (pid != null) {
					googleProjectId = String.valueOf(pid);
				}
			}
		} catch (Exception ignored) {
		}
	}
}
