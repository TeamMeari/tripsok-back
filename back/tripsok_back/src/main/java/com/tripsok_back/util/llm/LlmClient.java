package com.tripsok_back.util.llm;

import com.tripsok_back.type.LocaleCode;

public interface LlmClient {
    String requestGroqShortDescription(String prompt);
    String requestTranslation(String text, LocaleCode localeCode);
    String requestTransliteration(String text, LocaleCode localeCode);
    String sanitizeForVarchar(String input, int maxLen);
}

