package com.tripsok_back.dto.google;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class GoogleTranslateDto {

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class TranslateV3 {
		private List<String> contents;
		private String mimeType;
		private String sourceLanguageCode;
		private String targetLanguageCode;
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class TranslateV2 {
		private String q;
		private String source;
		private String target;
		private String format;
	}
}

