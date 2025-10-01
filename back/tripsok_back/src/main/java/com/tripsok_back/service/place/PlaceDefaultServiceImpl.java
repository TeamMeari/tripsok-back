package com.tripsok_back.service.place;

import static com.tripsok_back.exception.InternalErrorCode.*;

import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tripsok_back.config.ApiKeyConfig;
import com.tripsok_back.dto.PageResponse;
import com.tripsok_back.dto.place.PlaceBriefSlimResponseDto;
import com.tripsok_back.dto.place.PlaceDetailResponseDto;
import com.tripsok_back.dto.place.ReviewRequestDto;
import com.tripsok_back.exception.TourApiException;
import com.tripsok_back.repository.place.PlaceRepository;
import com.tripsok_back.service.search.PlaceEsService;
import com.tripsok_back.type.LocaleCode;
import com.tripsok_back.type.TourismType;
import com.tripsok_back.util.GoogleTranslateClient;
import com.tripsok_back.util.TouristApiClientUtil;
import com.tripsok_back.util.llm.LlmClient;

@Service
public class PlaceDefaultServiceImpl extends PlaceService {
	private final GoogleTranslateClient googleTranslateClient;

	public PlaceDefaultServiceImpl(PlaceRepository placeRepository,
		ApiKeyConfig apiKeyConfig, TouristApiClientUtil tourApiClient,
		CategoryService categoryService, LlmClient groqApiClientUtil,
		ObjectMapper om, GoogleTranslateClient googleTranslateClient, PlaceEsService placeEsService) {
		super(apiKeyConfig, tourApiClient, categoryService, groqApiClientUtil, om, googleTranslateClient,
			placeEsService, placeRepository);
		this.googleTranslateClient = googleTranslateClient;
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
	public PageResponse<PlaceBriefSlimResponseDto> getPlaceList(Pageable pageable, LocaleCode locale) {
		throw new TourApiException(CATEGORY_NOT_FOUND);
	}

	@Override
	public PageResponse<PlaceBriefSlimResponseDto> getPlaceListByTheme(Pageable pageable, Integer themeId,
		LocaleCode locale) {
		throw new TourApiException(CATEGORY_NOT_FOUND);
	}

	@Override
	public void addReview(Integer userId, ReviewRequestDto reviewRequestdto) {
		throw new TourApiException(CATEGORY_NOT_FOUND);
	}

	@Override
	public int reindexFullEs() {
		return 0;
	}
}
