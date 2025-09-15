package com.tripsok_back.service.user;

import com.tripsok_back.dto.SliceResponse;
import com.tripsok_back.model.user.TripSokUser;
import com.tripsok_back.type.LocaleCode;
import com.tripsok_back.type.PlaceJoinType;

public interface InterestPlaceService {
	void toggleInterestPlaces(TripSokUser user, Integer placeId);

	SliceResponse getUserLikedPlaces(TripSokUser user, Integer size, Integer lastId, PlaceJoinType type,
		LocaleCode locale);
}
