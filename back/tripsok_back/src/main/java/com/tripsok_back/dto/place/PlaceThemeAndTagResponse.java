package com.tripsok_back.dto.place;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tripsok_back.type.LocaleCode;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlaceThemeAndTagResponse {
	private Set<PlaceDto> places = new HashSet<>();

	@Getter
	@Setter
	public static class PlaceDto {
		private Integer contentId;
		private Set<String> theme;
		private List<TagDto> tag;
	}

	@Getter
	@Setter
	public static class TagDto {
		@JsonProperty("KO")
		private String KO;
		@JsonProperty("EN")
		private String EN;
		@JsonProperty("JP")
		private String JP;
		@JsonProperty("CN")
		private String CN;

		public String getNameByLocale(LocaleCode locale) {
			return switch (locale) {
				case KO -> KO;
				case EN -> EN;
				case JA -> JP;
				case CN -> CN;
			};
		}
	}
}
