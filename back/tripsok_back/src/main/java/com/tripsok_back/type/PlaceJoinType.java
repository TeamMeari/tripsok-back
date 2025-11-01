package com.tripsok_back.type;

import com.tripsok_back.dto.place.PlaceDocument;
import com.tripsok_back.exception.ErrorCode;
import com.tripsok_back.exception.PlaceException;
import com.tripsok_back.model.place.Place;

public enum PlaceJoinType {
	ACCOMMODATION, RESTAURANT, TOUR;

	public static PlaceJoinType getPlaceType(Place place) {
		if (place.getAccommodation() != null) {
			return PlaceJoinType.ACCOMMODATION;
		} else if (place.getRestaurant() != null) {
			return PlaceJoinType.RESTAURANT;
		} else if (place.getTour() != null) {
			return PlaceJoinType.TOUR;
		} else {
			throw new PlaceException(ErrorCode.INVALID_TOUR_TYPE);
		}
	}

	public static PlaceJoinType getPlaceType(PlaceDocument place) {
		if (place.getType() != null) {
			return PlaceJoinType.ACCOMMODATION;
		} else if (place.getType() != null) {
			return PlaceJoinType.RESTAURANT;
		} else if (place.getType() != null) {
			return PlaceJoinType.TOUR;
		} else {
			throw new PlaceException(ErrorCode.INVALID_TOUR_TYPE);
		}
	}
}
