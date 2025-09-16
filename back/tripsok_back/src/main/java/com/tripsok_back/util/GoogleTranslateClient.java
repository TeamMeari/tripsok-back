package com.tripsok_back.util;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import com.tripsok_back.config.ApiKeyConfig;
import com.tripsok_back.dto.google.GoogleTranslateDto;
import com.tripsok_back.type.LocaleCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class GoogleTranslateClient {

	private final ApiKeyConfig apiKeyConfig;
	private final ObjectMapper objectMapper = new ObjectMapper();
	private final HttpClient httpClient = HttpClient.newHttpClient();

	public String requestTranslation(String text, LocaleCode locale) {
		String target = locale.toGcpLang();
		try {
			return translateV3(text, target);
		} catch (Exception e) {
			throw new RuntimeException("번역 요청 실패", e);
		}
	}

	public String requestTransliteration(String text, LocaleCode localeCode) {
		List<com.tripsok_back.dto.google.RomanizationResult> results = TransliterationUtil.romanize(text, localeCode);
		return results.isEmpty() ? null : results.get(0).getRomanizedText();
	}

	private String translateV3(String text, String target) throws Exception {
		String endpointUrl = String.format(
			"https://translation.googleapis.com/v3/projects/%s/locations/%s:translateText",
			apiKeyConfig.getGoogleProjectId(), apiKeyConfig.getGoogleLocation());

		GoogleTranslateDto.TranslateV3 body = GoogleTranslateDto.TranslateV3.builder()
			.contents(List.of(text))
			.mimeType("text/plain")
			.sourceLanguageCode(LocaleCode.KO.toGcpLang())
			.targetLanguageCode(target)
			.build();

		String requestBodyJson = objectMapper.writeValueAsString(body);

		HttpRequest httpRequest = HttpRequest.newBuilder()
			.uri(URI.create(endpointUrl))
			.header("Content-Type", "application/json; charset=UTF-8")
			.header("Authorization", "Bearer " + getAccessToken())
			.POST(HttpRequest.BodyPublishers.ofString(requestBodyJson, StandardCharsets.UTF_8))
			.build();

		HttpResponse<String> httpResponse = httpClient.send(httpRequest,
			HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
		if (httpResponse.statusCode() / 100 != 2) {
			throw new RuntimeException(
				"GCP Translate(v3) failed: HTTP " + httpResponse.statusCode() + " - " + httpResponse.body());
		}

		Map<String, Object> parsedResponse = objectMapper.readValue(httpResponse.body(), new TypeReference<>() {
		});
		List<Map<String, Object>> translations = (List<Map<String, Object>>)parsedResponse.get(
			"translations");
		if (translations == null || translations.isEmpty()) {
			throw new RuntimeException("No translations in response");
		}
		Object translatedText = translations.get(0).get("translatedText");
		return translatedText == null ? null : String.valueOf(translatedText);
	}

	private String getAccessToken() throws Exception {
		GoogleCredentials creds;
		ClassPathResource keyRes = new ClassPathResource(
			"googleTranslate/translate-key.json");
		if (keyRes.exists()) {
			try (java.io.InputStream in = keyRes.getInputStream()) {
				creds = GoogleCredentials.fromStream(in);
			}
		} else {
			creds = GoogleCredentials.getApplicationDefault();
		}
		creds = creds.createScoped("https://www.googleapis.com/auth/cloud-translation");
		creds.refreshIfExpired();
		AccessToken token = creds.getAccessToken();
		if (token == null || token.getTokenValue() == null) {
			throw new IllegalStateException("Failed to obtain OAuth access token for Google Translation API");
		}
		return token.getTokenValue();
	}

}
