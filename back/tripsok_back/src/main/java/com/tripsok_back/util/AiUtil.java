package com.tripsok_back.util;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.azure.ai.inference.ChatCompletionsClient;
import com.azure.ai.inference.ChatCompletionsClientBuilder;
import com.azure.ai.inference.models.ChatCompletions;
import com.azure.ai.inference.models.ChatCompletionsOptions;
import com.azure.ai.inference.models.ChatCompletionsResponseFormatJsonObject;
import com.azure.ai.inference.models.ChatRequestMessage;
import com.azure.ai.inference.models.ChatRequestSystemMessage;
import com.azure.ai.inference.models.ChatRequestUserMessage;
import com.azure.core.credential.AzureKeyCredential;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tripsok_back.config.properties.AiProperties;
import com.tripsok_back.dto.place.PlaceThemeAndTagResponse;
import com.tripsok_back.exception.AiException;
import com.tripsok_back.exception.InternalErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiUtil {
	private static final List<String> MODELS = List.of(
		//"gpt-5", "gpt-5-mini", "gpt-5-nano"
		"gpt-4.1-mini", "gpt-4.1-nano"
	);
	private final ObjectMapper om;
	private static final String END_POINT = "https://models.inference.ai.azure.com";
	private static final String THEME_LIST = "바다, 자연, 힐링여행, 온천, 등산, K-POP, 미술관, 역사유적, 한옥, 축제, 맛집탐방, 전통시장, 카페투어, 쇼핑, 야경";
	private final AiProperties aiProperties;
	private final String GET_THEME_AND_TAG_PROMPT = String.join("\n",
		"You are a travel tagging assistant.",
		"The user will provide one or more place overviews.",
		"",
		"Follow these rules:",
		"1. Always respond in valid JSON format.",
		"2. The root object must contain exactly one field \"places\", which is an array.",
		"3. Each element of \"places\" must be an object with these three fields only:",
		"   - \"contentId\": the place contentId",
		"   - \"theme\": an array of 1–3 items, chosen only from this list: "
			+ THEME_LIST,
		"   - \"tag\": an array of 2–4 free-form tags, each a single word with ≤7 characters.",
		"4. When extracting tags, do not use broad categories such as “restaurant,” “travel,” or “accommodation,” nor brand names, place names, or city names.",
		"5. \"theme\" values must always be in Korean regardless of the input language.",
		"6. \"tag\" values must follow the input language (if input is English, tags must be English).",
		"7. Do not include any text outside the JSON.",
		"",
		"Example of the expected format:",
		"{",
		"  \"places\": [",
		"    {",
		"      \"contentId\": \"2913235\",",
		"      \"theme\": [\"맛집탐방\", \"바다\"],",
		"      \"tag\": [\"대게\", \"킹크랩\", \"수족관\", \"신선함\"]",
		"    },",
		"    {",
		"      \"contentId\": \"2913433\",",
		"      \"theme\": [\"카페투어\", \"자연\", \"힐링여행\"],",
		"      \"tag\": [\"호수뷰\", \"테라스\", \"베이커리\", \"반려견\"]",
		"    }",
		"  ]",
		"}"
	);

	public PlaceThemeAndTagResponse getThemeAndTag(String question) {
		ArrayList<ChatRequestMessage> chatMessages = new ArrayList<>();
		ObjectMapper om = new ObjectMapper();
		String payload;
		try {
			payload = om.writeValueAsString(question);
		} catch (JsonProcessingException e) {
			log.error("AI 요청 파싱 에러 : {}", e.getMessage());
			throw new RuntimeException(e);
		}
		chatMessages.add(new ChatRequestSystemMessage(GET_THEME_AND_TAG_PROMPT));
		chatMessages.add(new ChatRequestUserMessage(payload));

		ChatCompletionsOptions chatCompletionsOptions = new ChatCompletionsOptions(chatMessages);
		ChatCompletions completions = getChatComplete(0, chatCompletionsOptions, 0);
		try {
			return om.readValue(completions.getChoices().getFirst().getMessage().getContent(),
				PlaceThemeAndTagResponse.class);
		} catch (Exception e) {
			log.error("AI 응답 파싱 에러 : {}", e.getMessage());
			return new PlaceThemeAndTagResponse();
		}
	}

	private ChatCompletions getChatComplete(int modelIndex, ChatCompletionsOptions chatCompletionsOptions,
		int retryCount) { //TODO : retryCount 제거
		ChatCompletionsClient chatCompletionsClient = new ChatCompletionsClientBuilder()
			.credential(new AzureKeyCredential(aiProperties.getGithubToken()))
			.endpoint(END_POINT)
			.buildClient();
		chatCompletionsOptions.setModel(MODELS.get(modelIndex));
		chatCompletionsOptions.setResponseFormat(new ChatCompletionsResponseFormatJsonObject());
		try {
			return chatCompletionsClient.complete(chatCompletionsOptions);
		} catch (Exception e) {
			log.warn("모델 선택 에러 : {} / 모델 : {}", e.getMessage(), MODELS.get(modelIndex));
			if (retryCount == 2) {
				throw new AiException(InternalErrorCode.RETRIES_EXCEEDED_ERROR, e.getMessage());
			}
			return getChatComplete((modelIndex + 1) % MODELS.size(), chatCompletionsOptions, retryCount + 1);
		}
	}
}
