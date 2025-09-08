package com.tripsok_back.dto.groq;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OllamaChatRequest {
	private String model;
	private List<OllamaMessage> messages;
	private OllamaOptions options;
	private boolean stream; // false로 두면 단일 응답
}

