package com.tripsok_back.util;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class GroqApiClientUtilsave {
/*
	private final WebClient groqApiWebClient;
	private final ObjectMapper objectMapper;
	private final ApiKeyConfig apiKeyConfig;

	private static final String shortDescriptionPrompt =
		"You are a copywriter who introduces tourist spots in a catchy and emotional way.\n"
			+ "Output Rules\n"
			+ "Produce only one sentence.\n"
			+ "The sentence must be within 15 Korean characters (at least 10 recommended).\n"
			+ "The ending must be cut off naturally at a keyword (single word ending).\n"
			+ "Output must be plain Korean text only (no emojis, no Markdown, no commas).\n"
			+ "Always reflect the tourist spot and its key features.\n"
			+ "If the input is unrelated to tourist spots, answer only with “해당 없음”.\n"
			+ "or 사용자에게 노출되는 결과값이라면 : “상세 정보 확인 하기”\n"
			+ "Tone & Style\n"
			+ "Optimized for SNS-style catchy and viral copy\n"
			+ "Short, memorable, and easy to share\n"
			+ "Should feel natural, like a photo caption";
	private static final String translationBasePrompt =
		"You are a professional travel copy translator.\n"
			+ "Task\n"
			+ "- Translate the following Korean accommodation description into {language}.\n"
			+ "- Produce natural, fluent, marketing-friendly prose suitable for hotel listings.\n"
			+ "- Fix spacing, grammar, and awkward phrasing while preserving meaning.\n"
			+ "Output Rules\n"
			+ "- Output only in {language}. Do NOT include any Korean (Hangul) text.\n"
			+ "- Do NOT wrap with quotes, brackets, or explanations.\n"
			+ "- No backticks, no markdown.\n"
			+ "Proper Nouns & Places\n"
			+ "- Do not leave Hangul. Localize place names using widely used forms in {language}.\n"
			+ "- If a common exonym exists, use it; otherwise transliterate into the target script.\n"
			+ "- Keep brand/property names as proper nouns, rendered in the target script.\n";
	private static final String transliterationPrompt =
		"Transliterate the following Korean text into {language} phonetic form only.\n"
			+ "- Keep proper nouns recognizable.\n"
			+ "- Do NOT translate meanings, only render pronunciation in target script.\n"
			+ "- Output plain text with no quotes or extra comments.";

	public String requestGroqShortDescription(String prompt) {

		String adjustPrompt = shortDescriptionPrompt + prompt;

		try {
			ChatCompletionRequest request = ChatCompletionRequest.builder()
				.model("llama-3.1-8b-instant")
				.messages(List.of(
					new GroqMessage("user", adjustPrompt)
				))
				.build();

			ChatCompletionResponse response = groqApiWebClient
				.post()
				.uri("https://api.groq.com/openai/v1/chat/completions")
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKeyConfig.getGroqApiKey())
				.bodyValue(objectMapper.writeValueAsString(request))
				.retrieve()
				.bodyToMono(ChatCompletionResponse.class)
				.block();

			if (response != null && response.getChoices() != null && !response.getChoices().isEmpty()) {
				return sanitizePlainText(response.getChoices().get(0).getMessage().getContent());
			}
		} catch (Exception e) {
			log.error("Groq API 요청 실패: {}", e.getMessage(), e);
		}
		return "응답을 불러올 수 없습니다.";
	}

	public String requestTranslation(String summary, LocaleCode localeCode) {
		String text = summary == null ? "" : summary;
		String guidelines = buildTranslationGuidelines(localeCode);
		String adjustPrompt = guidelines + "\n\n" + text;

		try {
			ChatCompletionRequest request = ChatCompletionRequest.builder()
				.model("llama-3.1-8b-instant")
				.messages(List.of(
					new GroqMessage("user", adjustPrompt)
				))
				.build();

			ChatCompletionResponse response = groqApiWebClient
				.post()
				.uri("https://api.groq.com/openai/v1/chat/completions")
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKeyConfig.getGroqApiKey())
				.bodyValue(objectMapper.writeValueAsString(request))
				.retrieve()
				.bodyToMono(ChatCompletionResponse.class)
				.block();

			if (response != null && response.getChoices() != null && !response.getChoices().isEmpty()) {
				return sanitizePlainText(response.getChoices().get(0).getMessage().getContent());
			}
		} catch (Exception e) {
			log.error("Groq API 요청 실패: {}", e.getMessage(), e);
		}
		return "응답을 불러올 수 없습니다.";
	}

	public String requestTransliteration(String text, LocaleCode localeCode) {
		String base = text == null ? "" : text;
		String adjustPrompt = buildTransliterationGuidelines(localeCode) + "\n\n" + base;

		try {
			ChatCompletionRequest request = ChatCompletionRequest.builder()
				.model("llama-3.1-8b-instant")
				.messages(List.of(
					new GroqMessage("user", adjustPrompt)
				))
				.build();

			ChatCompletionResponse response = groqApiWebClient
				.post()
				.uri("https://api.groq.com/openai/v1/chat/completions")
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKeyConfig.getGroqApiKey())
				.bodyValue(objectMapper.writeValueAsString(request))
				.retrieve()
				.bodyToMono(ChatCompletionResponse.class)
				.block();

			if (response != null && response.getChoices() != null && !response.getChoices().isEmpty()) {
				return sanitizePlainText(response.getChoices().get(0).getMessage().getContent());
			}
		} catch (Exception e) {
			log.error("Groq API 요청 실패: {}", e.getMessage(), e);
		}
		return "";
	}

	private String buildTranslationGuidelines(LocaleCode locale) {
		String base = translationBasePrompt.replace("{language}", locale.getLanguage());
		String localeAddendum = switch (locale) {
			case EN -> "\nLocale Notes\n"
				+ "- Use clear, natural English suitable for international travelers.\n"
				+ "- For Korean locations, use standard Revised Romanization (e.g., Gangwon-do, Goseong).\n";
			case JA -> "\nLocale Notes\n"
				+ "- Use natural Japanese.\n"
				+ "- Prefer common kanji exonyms for Korean places when they exist (e.g., 江原道, 高城).\n"
				+ "- If unknown, use katakana for transliteration.\n";
			case CN -> "\nLocale Notes\n"
				+ "- Use natural Traditional Chinese.\n"
				+ "- Prefer common Chinese exonyms for Korean places; if unknown, use Hanyu Pinyin (no tone marks).\n";
			case KO -> "";
		};
		return base + localeAddendum;
	}

	private String buildTransliterationGuidelines(LocaleCode locale) {
		// Ensure we never emit Hangul in target outputs and choose the right script
		String base = transliterationPrompt.replace("{language}", locale.getLanguage());
		String localeAddendum = switch (locale) {
			case EN -> "\nLocale Notes\n"
				+ "- Use Revised Romanization for Korean (no diacritics).\n";
			case JA -> "\nLocale Notes\n"
				+ "- Use katakana for phonetic rendering.\n";
			case CN -> "\nLocale Notes\n"
				+ "- If a common Chinese name exists, output it; otherwise use Hanyu Pinyin (no tone marks).\n";
			case KO -> "";
		};
		return base + localeAddendum;
	}

	// --- Sanitization helpers to protect DB constraints and keep output clean ---
	public String sanitizePlainText(String input) {
		if (input == null)
			return null;
		String out = input
			.replaceAll("[\u0000-\u001F&&[^\r\n\t]]", "")
			.replace('\u200B', ' ')
			.replace('\u200C', ' ')
			.replace('\u200D', ' ')
			.replace('\u2060', ' ')
			.replace('\uFEFF', ' ')
			.replaceAll("\r?\n+", " ")
			.trim();
		if ((out.startsWith("`") && out.endsWith("`")) ||
			(out.startsWith("\"") && out.endsWith("\"")) ||
			(out.startsWith("'") && out.endsWith("'"))) {
			out = out.substring(1, out.length() - 1).trim();
		}
		out = out.replaceAll("\s{2,}", " ");
		return out;
	}

	public String sanitizeForVarchar(String input, int maxLen) {
		String out = sanitizePlainText(input);
		if (out == null)
			return null;
		if (out.length() > maxLen) {
			out = out.substring(0, maxLen);
		}
		return out;
	}

 */
}
