package com.tripsok_back.dto.place;

import java.util.Set;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlaceThemeAndTagResponse {
	private Set<Item> places;

	@Getter
	@Setter
	public static class Item {
		private Integer contentId;
		private Set<String> theme;
		private Set<String> tag;
	}
}
