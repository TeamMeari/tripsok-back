package com.tripsok_back.service.place;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Pageable;

import com.tripsok_back.dto.PageResponse;
import com.tripsok_back.dto.place.PlaceBriefResponseDto;
import com.tripsok_back.dto.place.PlaceDetailResponseDto;
import com.tripsok_back.dto.place.ReviewRequestDto;
import com.tripsok_back.model.place.Place;
import com.tripsok_back.model.place.PlaceTag;
import com.tripsok_back.type.TourismType;

public abstract class PlaceService {
	protected List<PlaceTag> getPlaceTagsByIds(List<Integer> tagIds) {
		return null;
	}

	protected Set<PlaceTag> getPlaceTags(Place place) {
		return place.getTags();
	}

	public abstract TourismType getType();

	public abstract void startPlaceUpdate(int numOfRow, int pageNo);

	public abstract Optional<PlaceDetailResponseDto> getPlaceDetail(int placeId);

	abstract void addView(Place place);

	public abstract void addLike(Place place);

	public abstract PageResponse<PlaceBriefResponseDto> getPlaceList(Pageable pageable);

	public abstract void addReview(Integer userId, ReviewRequestDto reviewRequestdto);
}
