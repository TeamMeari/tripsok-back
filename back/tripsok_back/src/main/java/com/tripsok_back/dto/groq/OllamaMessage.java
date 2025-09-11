package com.tripsok_back.dto.groq;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OllamaMessage {
	private String role;    // "user" / "system" / "assistant"
	private String content;
}
