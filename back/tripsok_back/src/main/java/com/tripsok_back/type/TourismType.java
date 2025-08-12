package com.tripsok_back.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum TourismType {
	TOURIST_SPOT(12, "관광지"),
	CULTURAL_FACILITY(14, "문화시설"),
	FESTIVAL_EVENT(15, "축제공연행사"),
	TRAVEL_COURSE(25, "여행코스"),
	LEISURE_SPORTS(28, "레포츠"),
	ACCOMMODATION(32, "숙박"),
	SHOPPING(38, "쇼핑"),
	RESTAURANT(39, "음식점");

	private final Integer id;
	private final String description;

	public static TourismType fromId(int id) {
		for (TourismType type : values()) {
			if (type.id == id) {
				return type;
			}
		}
		throw new IllegalArgumentException("Unknown TourismType id: " + id);
	}
}