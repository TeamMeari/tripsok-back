package com.tripsok_back.service.place;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.util.InternalException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tripsok_back.config.ApiKeyConfig;
import com.tripsok_back.dto.PageResponse;
import com.tripsok_back.dto.place.PlaceBriefResponseDto;
import com.tripsok_back.dto.place.PlaceDetailResponseDto;
import com.tripsok_back.dto.place.ReviewRequestDto;
import com.tripsok_back.dto.tourApi.TourApiPlaceDetailRequestDto;
import com.tripsok_back.dto.tourApi.TourApiPlaceDetailResponseDto;
import com.tripsok_back.dto.tourApi.TourApiPlaceRequestDto;
import com.tripsok_back.dto.tourApi.TourApiPlaceResponseDto;
import com.tripsok_back.exception.InternalErrorCode;
import com.tripsok_back.exception.TourApiException;
import com.tripsok_back.model.place.Place;
import com.tripsok_back.model.place.PlaceLclsCategory;
import com.tripsok_back.repository.place.RestaurantRepository;
import com.tripsok_back.type.LocaleCode;
import com.tripsok_back.type.PlaceJoinType;
import com.tripsok_back.type.TourismType;
import com.tripsok_back.util.GoogleTranslateClient;
import com.tripsok_back.util.JsonMapperUtil;
import com.tripsok_back.util.TimeUtil;
import com.tripsok_back.util.TouristApiClientUtil;
import com.tripsok_back.util.llm.LlmClient;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class RestaurantServiceImpl implements PlaceService {

	private final ApiKeyConfig apiKeyConfig;
	private final TouristApiClientUtil tourApiClient;
	private final RestaurantRepository restaurantRepository;
	private final CategoryService categoryService;
	private final ObjectMapper om;
	private final LlmClient groqApiClientUtil;
	private final GoogleTranslateClient googleTranslateClient;

	@Override
	public TourismType getType() {
		return TourismType.RESTAURANT;
	}

	@Override
	public void startPlaceUpdate(int numOfRow, int pageNo) {

		List<TourApiPlaceResponseDto> responseDtoList = requestPlace(numOfRow, pageNo);

		if (responseDtoList.isEmpty()) {
			log.info("응답받은 API 값이 없습니다");
			return;
		}
		for (TourApiPlaceResponseDto responseDto : responseDtoList) {
			checkAndUpdatePlace(responseDto);
		}
	}

	@Override
	@Transactional
	public Optional<PlaceDetailResponseDto> getPlaceDetail(int placeId, com.tripsok_back.type.LocaleCode locale) throws
		TourApiException {
		Optional<Place> optPlace = restaurantRepository.findById(placeId);
		if (optPlace.isEmpty())
			throw new TourApiException(InternalErrorCode.PLACE_DETAIL_NOT_FOUND);
		Place placeRestaurant = optPlace.get();
		if (placeRestaurant.getPlaceTr(LocaleCode.KO) == null ||
			!StringUtils.hasText(placeRestaurant.getPlaceTr(LocaleCode.KO).getSummary())) {
			createShortDescription(placeRestaurant);
		}
		if (locale != null && locale != LocaleCode.KO) {
			if (placeRestaurant.getPlaceTr(locale) == null ||
				!StringUtils.hasText(placeRestaurant.getPlaceTr(locale).getSummary())) {
				placeRestaurant.upsertTranslation(locale, null, null, null, null);
				createSummaryTranslation(placeRestaurant, locale);
			}
			createInformationTranslation(placeRestaurant, locale);
			createTransliterationForNameAndAddress(placeRestaurant, locale);
		}
		if (placeRestaurant.getRestaurant().getPlaceLclsCategory() == null ||
			(placeRestaurant.getRestaurant().getRestaurantImages() == null || placeRestaurant.getRestaurant()
				.getRestaurantImages()
				.isEmpty())) {
			TourApiPlaceDetailResponseDto tourApiPlaceDetailResponseDto = requestPlaceDetail(
				placeRestaurant.getContentId());
			PlaceLclsCategory category = categoryService.getCategoryByCode(
				tourApiPlaceDetailResponseDto.getCategoryLevel3());
			placeRestaurant.updateNullRestaurantDetail(tourApiPlaceDetailResponseDto, category);
		}
		addView(placeRestaurant);
		return Optional.of(PlaceDetailResponseDto.from(placeRestaurant, PlaceJoinType.RESTAURANT, locale));
	}

	@Override
	public void addView(Place place) {
		place.incrementViewCount();
	}

	@Override
	public void addLike(Place place) {
		place.incrementLikeCount();
	}

	public void removeLike(Place place) {

	}

	@Override
	public PageResponse<PlaceBriefResponseDto> getPlaceList(Pageable pageable,
		com.tripsok_back.type.LocaleCode locale) {
		Page<Place> placeList = restaurantRepository.findByRestaurantIsNotNull(pageable);
		if (placeList.getTotalPages() == 0)
			return PageResponse.empty();
		Page<PlaceBriefResponseDto> dtoList = placeList.map(
			e -> PlaceBriefResponseDto.from(e, getType().name(),
				e.getRestaurant().getImageUrlList().getFirst(),
				e.getRestaurant().getRestaurantImages().size(),
				e.getRestaurant().getRestaurantReviews().size(),
				locale));
		return PageResponse.fromPage(placeList, dtoList);
	}

	@Override
	public PageResponse<PlaceBriefResponseDto> getPlaceListByTheme(Pageable pageable, Integer themeId,
		LocaleCode locale) {
		Page<Place> placeList = restaurantRepository.findByRestaurantIsNotNullAndThemes_Theme_Id(pageable, themeId);
		Page<PlaceBriefResponseDto> dtoList = placeList.map(
			e -> PlaceBriefResponseDto.from(e, getType().name(),
				e.getRestaurant().getImageUrlList().getFirst(),
				e.getRestaurant().getRestaurantImages().size(),
				e.getRestaurant().getRestaurantReviews().size(),
				locale));
		return PageResponse.fromPage(placeList, dtoList);
	}

	@Override
	public void addReview(Integer userId, ReviewRequestDto reviewRequestdto) {

	}

	@Override
	public Page<Place> findAll(PageRequest of) {
		return restaurantRepository.findAllByRestaurantIsNotNull(of);
	}

	public List<TourApiPlaceResponseDto> requestPlace(int numOfRow, int pageNo) {
		TourApiPlaceRequestDto restaurantRequestDto = TourApiPlaceRequestDto.builder()
			.numOfRows(numOfRow)
			.pageNo(pageNo)
			.mobileOS("ETC")
			.mobileApp("tripsok-batch")
			.type("json")
			.arrange("R")
			.areaCode("32")
			.contentTypeId(this.getType().getId())
			.serviceKey(apiKeyConfig.getTourApiKey())
			.build();
		List<TourApiPlaceResponseDto> responseDtoList = tourApiClient.fetchPlaceData(restaurantRequestDto);
		if (!responseDtoList.isEmpty()) {
			log.info("RequestPlace: {}개 응답 성공 (미리보기): {}", responseDtoList.size(),
				JsonMapperUtil.pretty(om, responseDtoList.getFirst()));
		}
		return responseDtoList;
	}

	public TourApiPlaceDetailResponseDto requestPlaceDetail(Integer contentId) throws InternalException {
		TourApiPlaceDetailRequestDto restaurantRequestDto = TourApiPlaceDetailRequestDto.builder()
			.mobileOS("ETC")
			.mobileApp("tripsok-batch")
			.responseType("json")
			.contentId(contentId)
			.serviceKey(apiKeyConfig.getTourApiKey())
			.build();
		TourApiPlaceDetailResponseDto responseDto = tourApiClient.fetchPlaceDataDetail(restaurantRequestDto);

		log.info("RequestPlace: 상세정보 조회 성공 (미리보기): {}", JsonMapperUtil.pretty(om, responseDto));
		return responseDto;
	}

	@Transactional
	public Boolean checkAndUpdatePlace(TourApiPlaceResponseDto placeDto) {
		LocalDateTime placeUpdatedAt = TimeUtil.stringToLocalDateTime(placeDto.getModifiedTime());
		Optional<Place> place = restaurantRepository.findByContentId(placeDto.getContentId());
		if (place.isEmpty()) {
			addPlace(placeDto);
			log.info("식당: ContentId:{} 신규 항목으로 추가", placeDto.getContentId());
			return true;
		}
		Place placeData = place.get();
		if (placeData.getUpdatedAt().equals(placeUpdatedAt)) {
			log.info("식당: ContentId:{} 변경사항 없음", placeDto.getContentId());
			return false;
		} else {
			updatePlace(placeData, placeDto);
			log.info("식당: ContentId:{} 변경사항으로 업데이트 진행", placeDto.getContentId());
			return true;
		}
	}

	private void createShortDescription(Place place) {
		if (place.getPlaceTr(LocaleCode.KO) == null) {
			place.initPlaceTrsWithKorean(null, null, null, null);
		}
		String name = place.getPlaceTr(LocaleCode.KO) != null ? place.getPlaceTr(LocaleCode.KO).getPlaceName() : null;
		String source =
			place.getPlaceTr(LocaleCode.KO) != null ? place.getPlaceTr(LocaleCode.KO).getInformation() : null;
		log.info("createShortDescription: placeId={}, contentId={}, locale=KO, name='{}'",
			place.getId(), place.getContentId(), name);
		if (!StringUtils.hasText(source)) {
			log.info("createShortDescription: source empty, skip (placeId={})", place.getId());
			return;
		}
		String shortDescription = groqApiClientUtil.requestGroqShortDescription(source);
		if (place.getPlaceTr(LocaleCode.KO) != null) {
			String safe = groqApiClientUtil.sanitizeForVarchar(shortDescription, 255);
			place.getPlaceTr(LocaleCode.KO).setSummary(safe);
		}
	}

	private void createSummaryTranslation(Place place, LocaleCode locale) {
		if (place.getPlaceTr(locale) != null &&
			StringUtils.hasText(place.getPlaceTr(locale).getSummary())) {
			return;
		}
		String name = place.getPlaceTr(LocaleCode.KO) != null ? place.getPlaceTr(LocaleCode.KO).getPlaceName() : null;
		String base = place.getPlaceTr(LocaleCode.KO) != null ? place.getPlaceTr(LocaleCode.KO).getSummary() : null;
		log.info("createTranslation: placeId={}, contentId={}, locale={}, name='{}'",
			place.getId(), place.getContentId(), locale != null ? locale.getCode() : null, name);
		if (!StringUtils.hasText(base)) {
			log.info("createTranslation: KO summary missing/empty, skip (placeId={}, locale={})", place.getId(),
				locale != null ? locale.getCode() : null);
			return;
		}
		String translation = googleTranslateClient.requestTranslation(base, locale);
		translation = groqApiClientUtil.sanitizeForVarchar(translation, 255);
		place.getPlaceTr(locale).setSummary(translation);
	}

	private void createInformationTranslation(Place place, LocaleCode locale) {
		String info = place.getPlaceTr(LocaleCode.KO) != null ? place.getPlaceTr(LocaleCode.KO).getInformation() : null;
		if (!StringUtils.hasText(info))
			return;
		if (place.getPlaceTr(locale) == null) {
			place.upsertTranslation(locale, null, null, null, null);
		}
		if (StringUtils.hasText(place.getPlaceTr(locale).getInformation()))
			return;
		String translated = googleTranslateClient.requestTranslation(info, locale);
		place.getPlaceTr(locale).setInformation(translated);
	}

	private void createTransliterationForNameAndAddress(Place place, LocaleCode locale) {
		if (locale == null || locale == LocaleCode.KO)
			return;
		if (place.getPlaceTr(LocaleCode.KO) == null)
			return;
		String koName = place.getPlaceTr(LocaleCode.KO).getPlaceName();
		String koAddr = place.getPlaceTr(LocaleCode.KO).getAddress();
		if (place.getPlaceTr(locale) == null) {
			place.upsertTranslation(locale, null, null, null, null);
		}
		if (StringUtils.hasText(koName) && !StringUtils.hasText(place.getPlaceTr(locale).getPlaceName())) {
			String namePhonetic = googleTranslateClient.requestTransliteration(koName, locale);
			namePhonetic = groqApiClientUtil.sanitizeForVarchar(namePhonetic, 255);
			place.getPlaceTr(locale).setPlaceName(namePhonetic);
		}
		if (StringUtils.hasText(koAddr) && !StringUtils.hasText(place.getPlaceTr(locale).getAddress())) {
			String addrPhonetic = googleTranslateClient.requestTransliteration(koAddr, locale);
			addrPhonetic = groqApiClientUtil.sanitizeForVarchar(addrPhonetic, 255);
			place.getPlaceTr(locale).setAddress(addrPhonetic);
		}
	}

	public void updatePlace(Place existingPlace, TourApiPlaceResponseDto placeDto) {
		TourApiPlaceDetailResponseDto detailResponseDto = requestPlaceDetail(existingPlace.getContentId());
		log.info("updatePlace: 상세정보 응답 성공 (미리보기):  (pretty)\n{}",
			JsonMapperUtil.pretty(om, detailResponseDto));
		PlaceLclsCategory category = categoryService.getCategoryByCode(
			detailResponseDto.getLargeClassificationSystem3());
		existingPlace.updateRestaurant(placeDto, detailResponseDto, category);
		if (existingPlace.getRestaurant() != null && existingPlace.getRestaurant().getRestaurantImages() == null) {
			existingPlace.updateNullRestaurantDetail(detailResponseDto, category);
		}
		for (LocaleCode lc : LocaleCode.values()) {
			if (lc == LocaleCode.KO)
				continue;
			createSummaryTranslation(existingPlace, lc);
			createInformationTranslation(existingPlace, lc);
			createTransliterationForNameAndAddress(existingPlace, lc);
		}
		restaurantRepository.save(existingPlace);
		log.info("상세정보 업데이트 완료 : contentId={}, placeId={}, title={}, categoryName={}",
			existingPlace.getContentId(),
			existingPlace.getId(),
			existingPlace.getRestaurant() != null ? existingPlace.getRestaurant().getId() : null,
			existingPlace.getRestaurant() != null ? existingPlace.getRestaurant().getPlaceLclsCategory() : null
		);
	}

	public void addPlace(TourApiPlaceResponseDto placeDto) {
		TourApiPlaceDetailResponseDto detailResponseDto = requestPlaceDetail(placeDto.getContentId());
		log.info("addPlace: 상세정보 응답 성공 (미리보기): (pretty)\n{}",
			JsonMapperUtil.pretty(om, detailResponseDto));
		PlaceLclsCategory category = categoryService.getCategoryByCode(
			detailResponseDto.getLargeClassificationSystem3());
		Place restaurantPlace = Place.buildRestaurant(placeDto, detailResponseDto, category);
		createShortDescription(restaurantPlace);
		for (LocaleCode localeCode : LocaleCode.values()) {
			if (localeCode.equals(LocaleCode.KO))
				continue;
			createSummaryTranslation(restaurantPlace, localeCode);
			createInformationTranslation(restaurantPlace, localeCode);
			createTransliterationForNameAndAddress(restaurantPlace, localeCode);
		}
		restaurantRepository.save(restaurantPlace);
	}

}
