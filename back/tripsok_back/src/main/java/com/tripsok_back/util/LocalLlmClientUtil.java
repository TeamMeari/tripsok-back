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
import com.tripsok_back.util.llm.LlmClient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class LocalLlmClientUtil implements LlmClient {

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
	private final WebClient ollamaApiWebClient;
	private final ObjectMapper objectMapper;

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
