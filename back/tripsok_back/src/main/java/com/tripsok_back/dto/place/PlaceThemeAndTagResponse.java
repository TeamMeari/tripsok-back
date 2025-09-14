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
		@JsonProperty("ko")
		private String ko;
		@JsonProperty("en")
		private String en;
		@JsonProperty("ja")
		private String ja;
		@JsonProperty("cn")
		private String cn;

		public String getNameByLocale(LocaleCode locale) {
			return switch (locale) {
				case KO -> ko;
				case EN -> en;
				case JA -> ja;
				case CN -> cn;
			};
		}
	}
}
