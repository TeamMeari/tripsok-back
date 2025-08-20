package com.tripsok_back.type;

import com.tripsok_back.exception.CustomException;
import com.tripsok_back.exception.ErrorCode;

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

	public static TourismType getTourismTypeById(String category) {
		if (category.equals("accommodation"))
			return ACCOMMODATION;
		if (category.equals("shopping"))
			return SHOPPING;
		if (category.equals("tour"))
			return TOURIST_SPOT;
		if (category.equals("festival"))
			return FESTIVAL_EVENT;
		throw new CustomException(ErrorCode.INVALID_TOUR_TYPE);
	}
}