package com.tripsok_back.service.place;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tripsok_back.config.ApiKeyConfig;
import com.tripsok_back.dto.PageResponse;
import com.tripsok_back.dto.place.PlaceBriefSlimResponseDto;
import com.tripsok_back.dto.place.PlaceDetailResponseDto;
import com.tripsok_back.dto.place.ReviewRequestDto;
import com.tripsok_back.dto.tourApi.TourApiPlaceDetailRequestDto;
import com.tripsok_back.dto.tourApi.TourApiPlaceDetailResponseDto;
import com.tripsok_back.dto.tourApi.TourApiPlaceRequestDto;
import com.tripsok_back.dto.tourApi.TourApiPlaceResponseDto;
import com.tripsok_back.exception.InternalErrorCode;
import com.tripsok_back.exception.ServiceBlockException;
import com.tripsok_back.exception.TourApiException;
import com.tripsok_back.model.place.Place;
import com.tripsok_back.model.place.PlaceLclsCategory;
import com.tripsok_back.repository.place.AccommodationRepository;
import com.tripsok_back.repository.place.PlaceRepository;
import com.tripsok_back.service.search.PlaceEsService;
import com.tripsok_back.type.LocaleCode;
import com.tripsok_back.type.PlaceJoinType;
import com.tripsok_back.type.TourismType;
import com.tripsok_back.util.GoogleTranslateClient;
import com.tripsok_back.util.JsonMapperUtil;
import com.tripsok_back.util.TimeUtil;
import com.tripsok_back.util.TouristApiClientUtil;
import com.tripsok_back.util.llm.LlmClient;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AccommodationServiceImpl extends PlaceService {
	private final AccommodationRepository accommodationRepository;

	public AccommodationServiceImpl(PlaceRepository placeRepository, ApiKeyConfig apiKeyConfig,
		TouristApiClientUtil tourApiClient, AccommodationRepository accommodationRepository,
		CategoryService categoryService,
		ObjectMapper om, LlmClient groqApiClientUtil, GoogleTranslateClient googleTranslateClient,
		PlaceEsService placeEsService) {
		super(apiKeyConfig, tourApiClient, categoryService, groqApiClientUtil, om, googleTranslateClient,
			placeEsService, placeRepository);
		this.accommodationRepository = accommodationRepository;
	}

	@Override
	public TourismType getType() {
		return TourismType.ACCOMMODATION;
	}

	@Override
	public void startPlaceUpdate(int numOfRow, int pageNo) throws ServiceBlockException {
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
	public Optional<PlaceDetailResponseDto> getPlaceDetail(int placeId, LocaleCode locale) throws
		TourApiException {
		Optional<Place> optPlace = accommodationRepository.findById(placeId);
		if (optPlace.isEmpty())
			throw new TourApiException(InternalErrorCode.PLACE_DETAIL_NOT_FOUND);
		Place placeAccommodation = optPlace.get();
		if (placeAccommodation.getPlaceTr(LocaleCode.KO) == null ||
			!StringUtils.hasText(placeAccommodation.getPlaceTr(LocaleCode.KO).getSummary())) {
			createShortDescription(placeAccommodation);
		}
		if (locale != null && locale != LocaleCode.KO) {
			if (placeAccommodation.getPlaceTr(locale) == null ||
				!StringUtils.hasText(placeAccommodation.getPlaceTr(locale).getSummary())) {
				placeAccommodation.upsertTranslation(locale, null, null, null, null);
				createSummaryTranslation(placeAccommodation, locale);
			}
			createInformationTranslation(placeAccommodation, locale);
			createTransliterationForNameAndAddress(placeAccommodation, locale);
		}
		if (placeAccommodation.getAccommodation().getPlaceLclsCategory() == null) {
			TourApiPlaceDetailResponseDto tourApiPlaceDetailResponseDto = requestPlaceDetail(
				placeAccommodation.getContentId());
			PlaceLclsCategory category = categoryService.getCategoryByCode(
				tourApiPlaceDetailResponseDto.getCategoryLevel3());
			placeAccommodation.updateNullAccommodationDetail(tourApiPlaceDetailResponseDto, category);
		}
		log.info("request detail 카테고리 조회 {}",
			placeAccommodation.getAccommodation().getPlaceLclsCategory().getLclsSystm3Name());
		addView(placeAccommodation);
		return Optional.of(PlaceDetailResponseDto.from(placeAccommodation, PlaceJoinType.ACCOMMODATION, locale));
	}

	@Override
	public PageResponse<PlaceBriefSlimResponseDto> getPlaceList(Pageable pageable,
		LocaleCode locale) throws TourApiException {

		Page<Place> placePage = accommodationRepository.findByAccommodationIsNotNullAndPlaceTrs_Id_Locale(
			locale.getCode(), pageable);
		if (placePage.isEmpty())
			return PageResponse.empty();

		Place first = placePage.getContent().get(0);
		int trSize = first.getPlaceTrs() != null ? first.getPlaceTrs().size() : 0;
		log.info("getPlaceList: {}, {}, {}", placePage.getNumberOfElements(), trSize, first.getId());
		if (placePage.getTotalPages() == 0)
			return PageResponse.empty();
		Page<PlaceBriefSlimResponseDto> dtoList = placePage.map(
			e -> PlaceBriefSlimResponseDto.from(e, getType().name(),
				e.getAccommodation().getImageUrlList().getFirst(),
				e.getAccommodation().getAccommodationImages().size(),
				e.getAccommodation().getAccommodationReviews().size(),
				locale));

		return PageResponse.fromPage(placePage, dtoList);
	}

	public PageResponse<PlaceBriefSlimResponseDto> getPlaceListByTheme(Pageable pageable, Integer themeId,
		LocaleCode locale) {
		Page<Place> placeList = accommodationRepository.findByAccommodationIsNotNullAndThemes_Theme_Id(pageable,
			themeId);
		Page<PlaceBriefSlimResponseDto> dtoList = placeList.map(
			e -> PlaceBriefSlimResponseDto.from(e, getType().name(),
				e.getAccommodation().getImageUrlList().getFirst(),
				e.getAccommodation().getAccommodationImages().size(),
				e.getAccommodation().getAccommodationReviews().size(),
				locale));
		return PageResponse.fromPage(placeList, dtoList);
	}

	@Override
	public void addReview(Integer userId, ReviewRequestDto reviewRequestdto) {

	}

	@Override
	public Page<Place> findAll(PageRequest of) {
		return accommodationRepository.findAllByAccommodationIsNotNull(of);
	}

	public List<TourApiPlaceResponseDto> requestPlace(int numOfRow, int pageNo) throws ServiceBlockException {
		TourApiPlaceRequestDto accommodationRequestDto = TourApiPlaceRequestDto.builder()
			.numOfRows(numOfRow)
			.pageNo(pageNo)
			.arrange("R")
			.areaCode("32")
			.contentTypeId(this.getType().getId())
			.serviceKey(apiKeyConfig.getTourApiKey())
			.build();
		List<TourApiPlaceResponseDto> responseDtoList = tourApiClient.fetchPlaceData(accommodationRequestDto);
		if (!responseDtoList.isEmpty()) {
			log.info("RequestPlace: {}개 응답 성공 (미리보기): {}", responseDtoList.size(),
				JsonMapperUtil.pretty(om, responseDtoList.getFirst()));
		}
		return responseDtoList;
	}

	public TourApiPlaceDetailResponseDto requestPlaceDetail(Integer contentId) throws
		ServiceBlockException {
		TourApiPlaceDetailRequestDto accommodationRequestDto = TourApiPlaceDetailRequestDto.builder()
			.contentId(contentId)
			.serviceKey(apiKeyConfig.getTourApiKey())
			.build();
		TourApiPlaceDetailResponseDto responseDto = tourApiClient.fetchPlaceDataDetail(accommodationRequestDto);

		log.info("RequestPlace: 상세정보 조회 성공 (미리보기): {}", JsonMapperUtil.pretty(om, responseDto));
		return responseDto;
	}

	@Transactional
	public Boolean checkAndUpdatePlace(TourApiPlaceResponseDto placeDto) {
		LocalDateTime placeUpdatedAt = TimeUtil.stringToLocalDateTime(placeDto.getModifiedTime());
		Optional<Place> place = accommodationRepository.findByContentId(placeDto.getContentId());
		if (place.isEmpty()) {
			log.info("숙소: ContentId:{} 신규 항목으로 추가", placeDto.getContentId());
			addPlace(placeDto);
			return true;
		}
		Place placeData = place.get();
		if (placeData.getUpdatedAt().equals(placeUpdatedAt)) {
			log.info("숙소: ContentId:{} 변경사항 없음", placeDto.getContentId());
			return false;
		} else {
			log.info("숙소: ContentId:{} 변경사항으로 업데이트 진행", placeDto.getContentId());
			updatePlace(placeData, placeDto);
			return true;
		}
	}

	public void createShortDescription(Place place) {
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

	public void createSummaryTranslation(Place place, LocaleCode locale) {
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

	public void createInformationTranslation(Place place, LocaleCode locale) {
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

	public void createTransliterationForNameAndAddress(Place place, LocaleCode locale) {
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

	public void updatePlace(Place existingPlace, TourApiPlaceResponseDto placeDto) throws ServiceBlockException {
		TourApiPlaceDetailResponseDto detailResponseDto = requestPlaceDetail(existingPlace.getContentId());
		log.info("updatePlace: 상세정보 응답 성공 (미리보기):  (pretty)\n{}",
			JsonMapperUtil.pretty(om, detailResponseDto));
		PlaceLclsCategory category = categoryService.getCategoryByCode(
			detailResponseDto.getLargeClassificationSystem3());
		existingPlace.updateAccommodation(placeDto, detailResponseDto, category);
		if (existingPlace.getAccommodation() != null &&
			existingPlace.getAccommodation().getAccommodationImages() == null) {
			existingPlace.updateNullAccommodationDetail(detailResponseDto, category);
		}
		for (LocaleCode lc : LocaleCode.values()) {
			if (lc == LocaleCode.KO)
				continue;
			createSummaryTranslation(existingPlace, lc);
			createInformationTranslation(existingPlace, lc);
			createTransliterationForNameAndAddress(existingPlace, lc);
		}
		accommodationRepository.save(existingPlace);
		log.info("상세정보 업데이트 완료 : contentId={}, placeId={}, title={}, categoryName={}",
			existingPlace.getContentId(),
			existingPlace.getId(),
			existingPlace.getAccommodation() != null ? existingPlace.getAccommodation().getId() : null,
			existingPlace.getAccommodation() != null ? existingPlace.getAccommodation().getPlaceLclsCategory() : null
		);
	}

	public void addPlace(TourApiPlaceResponseDto placeDto) {
		TourApiPlaceDetailResponseDto detailResponseDto = requestPlaceDetail(placeDto.getContentId());
		log.info("addPlace: 상세정보 응답 성공 (미리보기): (pretty)\n{}",
			JsonMapperUtil.pretty(om, detailResponseDto));
		PlaceLclsCategory category = categoryService.getCategoryByCode(
			detailResponseDto.getLargeClassificationSystem3());
		Place accommodationPlace = Place.buildAccommodation(placeDto, detailResponseDto, category);
		createShortDescription(accommodationPlace);
		for (LocaleCode localeCode : LocaleCode.values()) {
			if (localeCode.equals(LocaleCode.KO))
				continue;
			createSummaryTranslation(accommodationPlace, localeCode);
			createInformationTranslation(accommodationPlace, localeCode);
			createTransliterationForNameAndAddress(accommodationPlace, localeCode);
		}
		accommodationRepository.save(accommodationPlace);
	}

	@Override
	public int reindexFullEs() {
		int page = 0;
		int size = 500;
		int placeCount = 0;
		int docCount = 0;
		Page<Place> placePage;
		do {
			placePage = accommodationRepository.findAllByAccommodationIsNotNullOrderByIdAsc(PageRequest.of(page, size));
			for (Place e : placePage.getContent()) {
				docCount += placeEsService.indexPlaceDocuments(e);
				placeCount++;
			}
			page++;
		} while (!placePage.isEmpty());
		log.info("AccommodationFullIndex 완료 (places={}, docs={})", placeCount, docCount);
		return docCount;
	}

}
