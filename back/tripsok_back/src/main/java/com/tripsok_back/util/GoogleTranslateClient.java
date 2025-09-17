package com.tripsok_back.util;

import java.util.List;

import org.springframework.stereotype.Component;

import com.tripsok_back.config.ApiKeyConfig;
import com.tripsok_back.dto.google.RomanizationResult;
import com.tripsok_back.type.LocaleCode;

import com.google.cloud.translate.v3.LocationName;
import com.google.cloud.translate.v3.TranslateTextRequest;
import com.google.cloud.translate.v3.TranslateTextResponse;
import com.google.cloud.translate.v3.Translation;
import com.google.cloud.translate.v3.TranslationServiceClient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class GoogleTranslateClient {

    private final ApiKeyConfig apiKeyConfig;
    private final TranslationServiceClient translationServiceClient;

	public String requestTranslation(String text, LocaleCode locale) {
		String target = locale.toGcpLang();
		try {
			return translateV3(text, target);
		} catch (Exception e) {
			throw new RuntimeException("번역 요청 실패", e);
		}
	}

	public String requestTransliteration(String text, LocaleCode localeCode) {
		List<RomanizationResult> results = TransliterationUtil.romanize(text, localeCode);
		return results.isEmpty() ? null : results.get(0).getRomanizedText();
	}

    private String translateV3(String text, String target) throws Exception {
        String projectId = apiKeyConfig.getGoogleProjectId();
        String location = apiKeyConfig.getGoogleLocation();
        if (projectId == null || projectId.isBlank()) {
            throw new IllegalStateException("GOOGLE_PROJECT_ID 가 존재하지 않음");
        }

        LocationName parent = LocationName.of(projectId, location);
        TranslateTextRequest request = TranslateTextRequest.newBuilder()
            .setParent(parent.toString())
            .addAllContents(List.of(text))
            .setMimeType("text/plain")
            .setSourceLanguageCode(LocaleCode.KO.toGcpLang())
            .setTargetLanguageCode(target)
            .build();

        TranslateTextResponse response = translationServiceClient.translateText(request);
        if (response.getTranslationsCount() == 0) {
            throw new RuntimeException("번역이 응답하지 않음");
        }
        Translation first = response.getTranslations(0);
        return first.getTranslatedText();
    }
}

