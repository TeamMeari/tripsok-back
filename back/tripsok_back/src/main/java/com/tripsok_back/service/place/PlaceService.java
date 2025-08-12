package com.tripsok_back.service.place;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tripsok_back.type.TourismType;

public interface PlaceService {

	TourismType getType();

	void startPlaceUpdate(int numOfRow, int pageNo) throws JsonProcessingException;

}
