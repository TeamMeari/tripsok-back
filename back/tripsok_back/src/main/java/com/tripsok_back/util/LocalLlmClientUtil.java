package com.tripsok_back.util;

import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tripsok_back.dto.groq.OllamaChatRequest;
import com.tripsok_back.dto.groq.OllamaChatResponse;
import com.tripsok_back.dto.groq.OllamaMessage;
import com.tripsok_back.dto.groq.OllamaOptions;
import com.tripsok_back.type.LocaleCode;
import com.tripsok_back.util.llm.LlmClient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class LocalLlmClientUtil implements LlmClient {

	private final WebClient ollamaApiWebClient;
	private final ObjectMapper objectMapper;

	private static final String DEFAULT_MODEL = "qwen2.5:14b-instruct";

	private static final String shortDescriptionPrompt =
		"""
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

	private static final String translationBasePrompt =
		"""
			ROLE
			- Professional travel copy translator to {language}.
				
			INPUT IS: Korean accommodation/restaurant description.
				
			HARD CONSTRAINTS
			- Faithfulness > Fluency. Do NOT invent or relocate places/dishes/brands/awards.
			- Preserve all named entities and addresses. If a common exonym exists, use it; otherwise transliterate minimally for {language}.
			- Keep ALL numbers/years/counts (e.g., '18종', '80년생' → '80-year-old').
			- If a token is unknown, keep it verbatim.
			- Output: ONE paragraph in {language}, plain text only (no quotes/markdown/emojis).
			- Geography is Korea unless the input states otherwise.
				
			PROTECTED TOKENS (do not alter spelling except script rendering)
			- {protected_tokens}
				
			TERM MAP for {language} (use these exact terms; never substitute others)
			- {term_map}
				
			EXAMPLES OF PROHIBITED CHANGES
			- Do not change '속초' to '수원' or other cities.
			""";

	private static final String transliterationPrompt =
		"Transliterate the following Korean text into {language} phonetic form only.\n"
			+ "- Keep proper nouns recognizable.\n"
			+ "- Do NOT translate meanings, only render pronunciation in target script.\n"
			+ "- Output plain text with no quotes or extra comments.";

	private static final String SYSTEM_GUARD = """
		GENERAL RULES
		- Deterministic MT: do NOT add, drop, or change facts.
		- Preserve numbers, years, counts, units, punctuation, and line breaks.
		- No meta text such as: Revised:, SURE, 改正为..., Note:, Explanation:, etc.
		- Plain text only (no quotes, no markdown fences, no emojis).
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
	private static final String SYSTEM_STRICT =
		"You MUST output only the target language content with NO notes, NO explanations, "
			+ "NO parentheses, NO brackets, and NO foreign scripts. Output exactly one line. "
			+ "If uncertain, output the best-guess final text in the target language without commentary.";

	public String requestGroqShortDescription(String prompt) {
		String adjustPrompt = shortDescriptionPrompt + prompt;
		try {
			OllamaChatRequest request = OllamaChatRequest.builder()
				.model(DEFAULT_MODEL)
				.messages(List.of(new OllamaMessage("user", adjustPrompt)))
				.options(OllamaOptions.builder().temperature(0.2).num_ctx(8192).build())
				.stream(false)
				.build();

			OllamaChatResponse response = ollamaApiWebClient
				.post()
				.uri("/api/chat")
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.bodyValue(objectMapper.writeValueAsString(request))
				.retrieve()
				.bodyToMono(OllamaChatResponse.class)
				.block();

			if (response != null && response.getMessage() != null) {
				return sanitizePlainText(response.getMessage().getContent());
			}
		} catch (Exception e) {
			log.error("Ollama API 요청 실패: {}", e.getMessage(), e);
		}
		return "응답을 불러올 수 없습니다.";
	}

	public String requestTranslation(String summary, LocaleCode localeCode) {
		String text = summary == null ? "" : summary;

		var msgs = List.of(
			new OllamaMessage("system", SYSTEM_STRICT),
			new OllamaMessage("system", SYSTEM_GUARD),
			new OllamaMessage("system", MT_SYSTEM_PROMPT.replace("{language}", localeCode.getLanguage())),
			new OllamaMessage("user", text)
		);

		try {
			OllamaChatRequest request = OllamaChatRequest.builder()
				.model(DEFAULT_MODEL)
				.messages(msgs)
				.options(OllamaOptions.builder()
					.temperature(0.1)
					.num_ctx(8192)
					.build())
				.stream(false)
				.build();

			OllamaChatResponse response = ollamaApiWebClient
				.post()
				.uri("/api/chat")
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.bodyValue(request)
				.retrieve()
				.bodyToMono(OllamaChatResponse.class)
				.block();

			String out = (response != null && response.getMessage() != null)
				? sanitizePlainText(response.getMessage().getContent())
				: null;

			if (isMixedScripts(out, localeCode)) {
				out = forceTargetLanguageRewrite(out, localeCode); // 1회 강제 재작성
			}
			return out != null ? out : "응답을 불러올 수 없습니다.";
		} catch (Exception e) {
			log.error("Ollama 번역 실패: {}", e.getMessage(), e);
			return "응답을 불러올 수 없습니다.";
		}
	}

	private static boolean isMixedScripts(String s, LocaleCode locale) {
		if (s == null || s.isBlank())
			return false;

		String common = "0-9\\s\\p{Punct}";
		String allowed;
		switch (locale) {
			case KO -> allowed = "\\u1100-\\u11FF\\u3130-\\u318F\\uAC00-\\uD7A3";
			case EN -> allowed = "A-Za-z";
			case JA -> allowed = "\\u3040-\\u309F\\u30A0-\\u30FF\\u4E00-\\u9FFF\\u30FC\\u3001\\u3002\\u30FB";
			case CN -> allowed = "\\u4E00-\\u9FFF\\u3001\\u3002";
			default -> allowed = "";
		}
		String pattern = "^[[" + allowed + common + "]]+$";
		return !s.codePoints().allMatch(cp -> String.valueOf((char)cp).matches(pattern));
	}

	private String forceTargetLanguageRewrite(String mixed, LocaleCode locale) {
		var msgs = List.of(
			new OllamaMessage("system", SYSTEM_GUARD),
			new OllamaMessage("system", """
				Rewrite the text into {language} ONLY.
				- Remove or replace any foreign words/scripts with {language}.
				- Preserve meaning, numbers, line breaks. No meta text.
				""".replace("{language}", locale.getLanguage())),
			new OllamaMessage("user", mixed)
		);

		try {
			OllamaChatRequest req = OllamaChatRequest.builder()
				.model(DEFAULT_MODEL)
				.messages(msgs)
				.options(OllamaOptions.builder().temperature(0.0).num_ctx(4096).build())
				.stream(false)
				.build();

			OllamaChatResponse resp = ollamaApiWebClient.post()
				.uri("/api/chat")
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.bodyValue(req)
				.retrieve()
				.bodyToMono(OllamaChatResponse.class)
				.block();

			String out =
				(resp != null && resp.getMessage() != null) ? sanitizePlainText(resp.getMessage().getContent()) : mixed;
			return out;
		} catch (Exception e) {
			log.warn("force rewrite failed: {}", e.getMessage());
			return mixed;
		}
	}

	public String requestTransliteration(String text, LocaleCode localeCode) {
		String base = text == null ? "" : text;
		String adjustPrompt = buildTransliterationGuidelines(localeCode) + "\n\n" + base;

		try {
			OllamaChatRequest request = OllamaChatRequest.builder()
				.model(DEFAULT_MODEL)
				.messages(List.of(new OllamaMessage("user", adjustPrompt)))
				.options(OllamaOptions.builder().temperature(0.2).num_ctx(4096).build())
				.stream(false)
				.build();

			OllamaChatResponse response = ollamaApiWebClient
				.post()
				.uri("/api/chat")
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.bodyValue(objectMapper.writeValueAsString(request))
				.retrieve()
				.bodyToMono(OllamaChatResponse.class)
				.block();

			if (response != null && response.getMessage() != null) {
				return sanitizePlainText(response.getMessage().getContent());
			}
		} catch (Exception e) {
			log.error("Ollama API 요청 실패: {}", e.getMessage(), e);
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
		out = out.replaceAll("\\s{2,}", " ");
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
}
