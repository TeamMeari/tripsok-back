package com.tripsok_back.service.place;

import static com.tripsok_back.exception.ErrorCode.*;

import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tripsok_back.config.ApiKeyConfig;
import com.tripsok_back.dto.PageResponse;
import com.tripsok_back.dto.place.PlaceBriefResponseDto;
import com.tripsok_back.dto.place.PlaceDetailResponseDto;
import com.tripsok_back.dto.place.ReviewRequestDto;
import com.tripsok_back.exception.PlaceException;
import com.tripsok_back.model.place.Place;
import com.tripsok_back.repository.place.PlaceRepository;
import com.tripsok_back.type.LocaleCode;
import com.tripsok_back.type.TourismType;
import com.tripsok_back.util.TouristApiClientUtil;
import com.tripsok_back.util.llm.LlmClient;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class PlaceService {
	final ApiKeyConfig apiKeyConfig;
	final TouristApiClientUtil tourApiClient;
	final CategoryService categoryService;
	final LlmClient groqApiClientUtil;
	final ObjectMapper om;
	private final PlaceRepository placeRepository;

	@Transactional
	public void addLike(Place place) {
		place.incrementLikeCount();
	}

	@Transactional
	public void removeLike(Place place) {
		place.decrementLikeCount();
	}

	public Place findPlaceById(int placeId) {
		return placeRepository.findById(placeId).orElseThrow(() -> new PlaceException(PLACE_NOT_FOUND));
	}

	protected void addView(Place place) {
		place.incrementViewCount();
	}

	public abstract TourismType getType();

	public abstract void startPlaceUpdate(int numOfRow, int pageNo);

	public abstract Optional<PlaceDetailResponseDto> getPlaceDetail(int placeId, LocaleCode locale);

	public abstract PageResponse<PlaceBriefResponseDto> getPlaceList(Pageable pageable, LocaleCode locale);

	public abstract PageResponse<PlaceBriefResponseDto> getPlaceListByTheme(Pageable pageable, Integer themeId,
		LocaleCode locale);

	public abstract void addReview(Integer userId, ReviewRequestDto reviewRequestdto);
}
