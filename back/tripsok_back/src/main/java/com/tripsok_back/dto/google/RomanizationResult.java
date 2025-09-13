package com.tripsok_back.dto.google;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RomanizationResult {
    private String romanizedText;
    private String detectedLanguageCode;
}

