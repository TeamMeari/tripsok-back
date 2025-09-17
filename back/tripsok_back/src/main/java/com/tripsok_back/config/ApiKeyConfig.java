package com.tripsok_back.config;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Component
@Slf4j
public class ApiKeyConfig {

	@Value("${TOUR_API_KEY}")
	private String tourApiKey;

	@Value("${GROQ_API_KEY}")
	private String groqApiKey;

	@Value("${GOOGLE_API_KEY:}")
	private String googleApiKey;

	@Value("${GOOGLE_PROJECT_ID:}")
	private String googleProjectId;

	@Value("${GOOGLE_LOCATION:global}")
	private String googleLocation;

	@Value("${google.translate.keyJson:}")
	private String googleKeyJson;

    @PostConstruct
    void resolveProjectIdFromKeyIfMissing() {
        if (googleProjectId != null && !googleProjectId.isBlank()) {
			log.warn("Google project ID 가 비었습니다", googleProjectId);
            return;
        }
        try {
            if (googleKeyJson == null || googleKeyJson.isBlank()) return;
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> json = mapper.readValue(googleKeyJson, new TypeReference<>() {});
            Object pid = json.get("project_id");
            if (pid != null) {
                googleProjectId = String.valueOf(pid);
            }
        } catch (Exception ignored) {
        }
    }
}
