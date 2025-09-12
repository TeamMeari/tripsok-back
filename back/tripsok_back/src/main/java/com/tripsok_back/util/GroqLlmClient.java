package com.tripsok_back.util;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tripsok_back.dto.groq.ChatCompletionRequest;
import com.tripsok_back.dto.groq.ChatCompletionResponse;
import com.tripsok_back.dto.groq.GroqMessage;
import com.tripsok_back.type.LocaleCode;
import com.tripsok_back.util.llm.LlmClient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Primary
public class GroqLlmClient implements LlmClient {

    @Qualifier("groqApiWebClient")
    private final WebClient groqApiWebClient;
	private final ObjectMapper objectMapper;

	private static final String DEFAULT_MODEL = "llama-3.3-70b-versatile";
	private static final String shortDescriptionPrompt = """
		ROLE
		- You are a copywriter for tourist spots.
		
		TASK
		- Write exactly ONE Korean sentence about the given place.
		
		HARD CONSTRAINTS
		- Only use facts from the input. Do NOT add places, dishes, or claims.
		- Length: ≤ 15 Korean characters (10~15 권장).
		- End naturally on a meaningful keyword (no trailing quotes, punctuation, or particles like '이다', '합니다').
		- Plain Korean text only (no emojis, no English, no commas).
		- If the input is unrelated to tourist spots OR lacks usable info, output exactly: 해당 없음
		
		STYLE
		- Catchy, memorable, SNS-friendly.
		""";
	private static final String MT_SYSTEM_PROMPT = """
		You are a machine translation engine.
		Translate from Korean into {language}.
		Constraints:
		- Output MUST be 100% in {language} script/words only. Do not mix any other languages.
		- Keep the same line/paragraph structure as input.
		- Keep list markers (•, -, 1.), brackets (), [], {}, and inline formatting.
		- If unsure about a token, copy it verbatim or minimally transliterate into {language}.
		- Do not explain. Output only the translation in {language}.
		""";
	private static final String SYSTEM_GUARD = """
		GENERAL RULES
		- Deterministic MT: do NOT add, drop, or change facts.
		- Preserve numbers, years, counts, units, punctuation, and line breaks.
		- No meta text such as: Revised:, SURE, 改正为..., Note:, Explanation:, etc.
		- Plain text only (no quotes, no markdown fences, no emojis).
		""";
	private static final String transliterationPrompt =
		"Transliterate the following Korean text into {language} phonetic form only.\n"
			+ "- Keep proper nouns recognizable.\n"
			+ "- Do NOT translate meanings, only render pronunciation in target script.\n"
			+ "- Output plain text with no quotes or extra comments.";

	@Override
	public String requestGroqShortDescription(String prompt) {
		String adjust = shortDescriptionPrompt + prompt;
		try {
			ChatCompletionRequest req = ChatCompletionRequest.builder()
				.model(DEFAULT_MODEL)
				.messages(List.of(new GroqMessage("user", adjust)))
				.build();

			ChatCompletionResponse res = groqApiWebClient.post()
				.uri("/chat/completions")
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.bodyValue(objectMapper.writeValueAsString(req))
				.retrieve()
				.bodyToMono(ChatCompletionResponse.class)
				.block();

			if (res != null && res.getChoices() != null && !res.getChoices().isEmpty()) {
				return sanitizePlainText(res.getChoices().get(0).getMessage().getContent());
			}
		} catch (Exception e) {
			log.error("Groq API 요청 실패: {}", e.getMessage(), e);
		}
		return "응답을 불러올 수 없습니다.";
	}

	@Override
	public String requestTranslation(String text, LocaleCode locale) {
		String base = text == null ? "" : text;
		var msgs = List.of(
			new GroqMessage("system", SYSTEM_GUARD),
			new GroqMessage("system", MT_SYSTEM_PROMPT.replace("{language}", locale.getLanguage())),
			new GroqMessage("user", base)
		);
		try {
			ChatCompletionRequest req = ChatCompletionRequest.builder()
				.model(DEFAULT_MODEL)
				.messages(msgs)
				.build();

			ChatCompletionResponse res = groqApiWebClient.post()
				.uri("/chat/completions")
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.bodyValue(objectMapper.writeValueAsString(req))
				.retrieve()
				.bodyToMono(ChatCompletionResponse.class)
				.block();

			String out = (res != null && res.getChoices() != null && !res.getChoices().isEmpty())
				? sanitizePlainText(res.getChoices().get(0).getMessage().getContent())
				: null;
			return out != null ? out : "응답을 불러올 수 없습니다.";
		} catch (Exception e) {
			log.error("Groq 번역 실패: {}", e.getMessage(), e);
			return "응답을 불러올 수 없습니다.";
		}
	}

	@Override
	public String requestTransliteration(String text, LocaleCode localeCode) {
		String base = text == null ? "" : text;
		String adjustPrompt = transliterationPrompt.replace("{language}", localeCode.getLanguage()) + "\n\n" + base;

		try {
			ChatCompletionRequest req = ChatCompletionRequest.builder()
				.model(DEFAULT_MODEL)
				.messages(List.of(new GroqMessage("user", adjustPrompt)))
				.build();

			ChatCompletionResponse res = groqApiWebClient.post()
				.uri("/chat/completions")
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.bodyValue(objectMapper.writeValueAsString(req))
				.retrieve()
				.bodyToMono(ChatCompletionResponse.class)
				.block();

			if (res != null && res.getChoices() != null && !res.getChoices().isEmpty()) {
				return sanitizePlainText(res.getChoices().get(0).getMessage().getContent());
			}
		} catch (Exception e) {
			log.error("Groq API 요청 실패: {}", e.getMessage(), e);
		}
		return "";
	}

	private String sanitizePlainText(String input) {
		if (input == null)
			return null;
		String out = input
			.replaceAll("[\\u0000-\\u001F&&[^\\r\\n\\t]]", "")
			.replace('\u200B', ' ')
			.replace('\u200C', ' ')
			.replace('\u200D', ' ')
			.replace('\u2060', ' ')
			.replace('\uFEFF', ' ')
			.replaceAll("\\r?\\n+", " ")
			.trim();
		if ((out.startsWith("`") && out.endsWith("`")) ||
			(out.startsWith("\"") && out.endsWith("\"")) ||
			(out.startsWith("'") && out.endsWith("'"))) {
			out = out.substring(1, out.length() - 1).trim();
		}
		out = out.replaceAll("\\s{2,}", " ");
		return out;
	}

	@Override
	public String sanitizeForVarchar(String input, int maxLen) {
		String out = sanitizePlainText(input);
		if (out == null)
			return null;
		if (out.length() > maxLen) {
			out = out.substring(0, maxLen);
		}
		return out;
	}
}
