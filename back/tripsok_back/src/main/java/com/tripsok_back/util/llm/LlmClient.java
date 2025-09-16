package com.tripsok_back.util.llm;

public interface LlmClient {
	String requestGroqShortDescription(String prompt);

	String sanitizeForVarchar(String input, int maxLen);
}

