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
	private static final String END_POINT = "https://models.inference.ai.azure.com";
	private static final String THEME_LIST = "바다, 자연, 힐링여행, 온천, 등산, K-POP, 미술관, 역사유적, 한옥, 축제, 맛집탐방, 전통시장, 카페투어, 쇼핑, 야경";
	private final ObjectMapper om;
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
		"6. \"tag\" values must be provided in four languages: ko (Korean), en (English), ja (Japanese), and cn (Chinese). Each tag must include translations for all four languages.",
		"7. Do not include any text outside the JSON.",
		"",
		"Example of the expected format:",
		"""
			{
			  "places": [
			    {
			      "contentId": "2913235",
			      "theme": ["맛집탐방", "바다"],
			      "tag": [
			        {
			          "ko": "대게",
			          "en": "Snow Crab",
			          "ja": "ズワイガニ",
			          "cn": "雪蟹"
			        },
			        {
			          "ko": "킹크랩",
			          "en": "King Crab",
			          "ja": "キングクラブ",
			          "cn": "帝王蟹"
			        },
			        {
			          "ko": "수족관",
			          "en": "Aquarium",
			          "ja": "水族館",
			          "cn": "水族馆"
			        },
			        {
			          "ko": "신선함",
			          "en": "Freshness",
			          "ja": "新鮮さ",
			          "cn": "新鲜感"
			        }
			      ]
			    }
			  ]
			}"""
	);

	public PlaceThemeAndTagResponse getThemeAndTag(String question) {
		ArrayList<ChatRequestMessage> chatMessages = new ArrayList<>();
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
		ChatCompletions completions = getChatComplete(chatCompletionsOptions);
		try {
			return om.readValue(completions.getChoices().getFirst().getMessage().getContent(),
				PlaceThemeAndTagResponse.class);
		} catch (Exception e) {
			log.error("AI 응답 파싱 에러 : {}", e.getMessage());
			return new PlaceThemeAndTagResponse();
		}
	}

	private ChatCompletions getChatComplete(ChatCompletionsOptions chatCompletionsOptions) { //TODO : retryCount 제거
		for (String model : MODELS) {
			try {
				ChatCompletionsClient chatCompletionsClient = new ChatCompletionsClientBuilder()
					.credential(new AzureKeyCredential(aiProperties.getServiceKey()))
					.endpoint(END_POINT)
					.buildClient();
				chatCompletionsOptions.setModel(model);
				chatCompletionsOptions.setResponseFormat(new ChatCompletionsResponseFormatJsonObject());
				return chatCompletionsClient.complete(chatCompletionsOptions);
			} catch (Exception e) {
				log.warn("모델 {} 호출 실패: {}", model, e.getMessage(), e);
			}
		}
		throw new AiException(InternalErrorCode.RETRIES_EXCEEDED_ERROR, new RuntimeException("모든 AI 모델 호출에 실패했습니다."));
	}
}
