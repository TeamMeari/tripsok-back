package com.tripsok_back.dto.groq;

import java.util.List;

import lombok.Data;

/**
 * Groq Chat API 응답 DTO
 */
@Data
public class ChatCompletionResponse {

	private List<Choice> choices;

	@Data
	public static class Choice {
		private GroqMessage message;
	}
}
