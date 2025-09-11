package com.tripsok_back.dto.groq;

import lombok.Data;

@Data
public class OllamaChatResponse {
	private String model;
	private String created_at;
	private OllamaMessage message; // ← content 여기서 꺼냄
	private boolean done;
}
