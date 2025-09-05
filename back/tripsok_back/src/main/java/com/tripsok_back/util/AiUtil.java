package com.tripsok_back.util;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.azure.ai.inference.ChatCompletionsClient;
import com.azure.ai.inference.ChatCompletionsClientBuilder;
import com.azure.ai.inference.models.ChatChoice;
import com.azure.ai.inference.models.ChatCompletions;
import com.azure.ai.inference.models.ChatCompletionsOptions;
import com.azure.ai.inference.models.ChatRequestMessage;
import com.azure.ai.inference.models.ChatRequestSystemMessage;
import com.azure.ai.inference.models.ChatRequestUserMessage;
import com.azure.core.credential.AzureKeyCredential;
import com.tripsok_back.config.properties.AiProperties;
import com.tripsok_back.exception.AiException;
import com.tripsok_back.exception.InternalErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiUtil {
	private static final List<String> MODELS = List.of(
		"gpt-5", "gpt-5-mini", "gpt-5-nano"
	);
	private static final String END_POINT = "https://models.inference.ai.azure.com";
	private static final String THEME_LIST = "바다, 자연, 힐링여행, 온천, 등산, K-POP, 미술관, 역사유적, 한옥, 축제, 맛집탐방, 전통시장, 카페투어, 쇼핑, 야경";
	private final AiProperties aiProperties;
	private final String GET_THEME_AND_TAG_PROMPT = String.join("\n",
		"1. Responses must be in JSON format.",
		"2. Select 1–3 themes from the given theme list that best match the place introduced in the text, and return them in a field called 'theme'. : "
			+ THEME_LIST,
		"3. Extract 2–4 attraction points of the place as free-form tags and return them in a field called 'tag'.",
		"4. When extracting tags, do not use broad categories such as “restaurant,” “travel,” or “accommodation,” nor brand names, place names, or city names.",
		"5. Each tag must consist of a single word with no more than 7 characters. For example, instead of “sea view,” extract “sea” as the tag.",
		"6. The 'theme' response must always be in Korean regardless of the input language, but the 'tag' response must follow the input language. For example, if the query is in English, the tags must be in English.",
		"7. When the user provides overviews of multiple places, organize them for each place in the format of theme and tag, and return the result."
	);

	public List<ChatChoice> getThemeAndTag(String question) {
		ArrayList<ChatRequestMessage> chatMessages = new ArrayList<>();
		chatMessages.add(new ChatRequestSystemMessage(GET_THEME_AND_TAG_PROMPT));
		chatMessages.add(new ChatRequestUserMessage(question));

		ChatCompletionsOptions chatCompletionsOptions = new ChatCompletionsOptions(chatMessages);
		ChatCompletions completions = getChatComplete(0, chatCompletionsOptions, 0);

		return completions.getChoices();
		//return completions.getChoices().getFirst().getMessage().getContent();
	}

	private ChatCompletions getChatComplete(int modelIndex, ChatCompletionsOptions chatCompletionsOptions,
		int retryCount) { //TODO : retryCount 제거
		ChatCompletionsClient chatCompletionsClient = new ChatCompletionsClientBuilder()
			.credential(new AzureKeyCredential(aiProperties.getGithubToken()))
			.endpoint(END_POINT)
			.buildClient();
		chatCompletionsOptions.setModel(MODELS.get(modelIndex));
		try {
			return chatCompletionsClient.complete(chatCompletionsOptions);
		} catch (Exception e) {
			log.warn("모델 선택 에러 : {}", modelIndex);
			if (retryCount == 3) {
				throw new AiException(InternalErrorCode.RETRIES_EXCEEDED_ERROR, e.getMessage());
			}
			getChatComplete((modelIndex + 1) % MODELS.size(), chatCompletionsOptions, retryCount + 1);
		}
		return null;
	}
}
