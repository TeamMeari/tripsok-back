package com.tripsok_back.service.place;

import java.util.Optional;

import org.springframework.data.domain.Pageable;

import com.tripsok_back.dto.PageResponse;
import com.tripsok_back.dto.place.PlaceBriefResponseDto;
import com.tripsok_back.dto.place.PlaceDetailResponseDto;
import com.tripsok_back.dto.place.ReviewRequestDto;
import com.tripsok_back.model.place.Place;
import com.tripsok_back.type.TourismType;

public interface PlaceService {

	TourismType getType();

	void startPlaceUpdate(int numOfRow, int pageNo);

	Optional<PlaceDetailResponseDto> getPlaceDetail(int placeId);

	void addView(Place place);

	void addLike(Place place);

	PageResponse<PlaceBriefResponseDto> getPlaceList(Pageable pageable);

	PageResponse<PlaceBriefResponseDto> getPlaceListByTheme(Pageable pageable, Integer themeId);

	void addReview(Integer userId, ReviewRequestDto reviewRequestdto);
}
