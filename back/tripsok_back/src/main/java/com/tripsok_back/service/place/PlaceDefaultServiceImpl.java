package com.tripsok_back.service.place;

import static com.tripsok_back.exception.InternalErrorCode.*;

import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tripsok_back.config.ApiKeyConfig;
import com.tripsok_back.dto.PageResponse;
import com.tripsok_back.dto.place.PlaceBriefResponseDto;
import com.tripsok_back.dto.place.PlaceDetailResponseDto;
import com.tripsok_back.dto.place.ReviewRequestDto;
import com.tripsok_back.exception.TourApiException;
import com.tripsok_back.repository.place.PlaceRepository;
import com.tripsok_back.type.LocaleCode;
import com.tripsok_back.type.TourismType;
import com.tripsok_back.util.TouristApiClientUtil;
import com.tripsok_back.util.llm.LlmClient;

@Service
public class PlaceDefaultServiceImpl extends PlaceService {
	public PlaceDefaultServiceImpl(PlaceRepository placeRepository,
		ApiKeyConfig apiKeyConfig, TouristApiClientUtil tourApiClient,
		CategoryService categoryService, LlmClient groqApiClientUtil,
		ObjectMapper om) {
		super(placeRepository, apiKeyConfig, tourApiClient, categoryService, groqApiClientUtil, om);
	}

	@Override
	public TourismType getType() {
		return null;
	}

	@Override
	public void startPlaceUpdate(int numOfRow, int pageNo) {
		throw new TourApiException(CATEGORY_NOT_FOUND);
	}

	@Override
	public Optional<PlaceDetailResponseDto> getPlaceDetail(int placeId, LocaleCode locale) {
		throw new TourApiException(CATEGORY_NOT_FOUND);
	}

	@Override
	public PageResponse<PlaceBriefResponseDto> getPlaceList(Pageable pageable, LocaleCode locale) {
		throw new TourApiException(CATEGORY_NOT_FOUND);
	}

	@Override
	public void addReview(Integer userId, ReviewRequestDto reviewRequestdto) {
		throw new TourApiException(CATEGORY_NOT_FOUND);
	}
}
