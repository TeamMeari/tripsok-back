package com.tripsok_back.batch.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tripsok_back.batch.domain.TourismType;

public interface PlaceService {

	TourismType getType();

	void startPlaceUpdate(int numOfRow, int pageNo) throws JsonProcessingException;

	//List<TourApiPlaceResponseDto> requestPlace(int numOfRow, int pageNo) throws JsonProcessingException;

	//TourApiPlaceDetailResponseDto requestPlaceDetail(Integer contentId) throws JsonProcessingException;

	//Boolean checkAndUpdatePlace(TourApiPlaceResponseDto placeDto) throws JsonProcessingException;

	//void updatePlace();

	//void addPlace();

}
