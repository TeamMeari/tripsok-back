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
import com.tripsok_back.exception.TourApiException;
import com.tripsok_back.model.place.Place;
import com.tripsok_back.model.place.PlaceLclsCategory;
import com.tripsok_back.repository.place.PlaceRepository;
import com.tripsok_back.repository.place.TourRepository;
import com.tripsok_back.service.search.PlaceEsService;
import com.tripsok_back.type.LocaleCode;
import com.tripsok_back.type.PlaceJoinType;
import com.tripsok_back.type.TourismType;
import com.tripsok_back.util.GoogleTranslateClient;
import com.tripsok_back.util.JsonMapperUtil;
import com.tripsok_back.util.TimeUtil;
import com.tripsok_back.util.TouristApiClientUtil;
import com.tripsok_back.util.llm.LlmClient;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TourServiceImpl extends PlaceService {
	private final TourRepository tourRepository;

	public TourServiceImpl(PlaceRepository placeRepository, ApiKeyConfig apiKeyConfig,
		TouristApiClientUtil tourApiClient, TourRepository tourRepository, CategoryService categoryService,
		ObjectMapper om, LlmClient groqApiClientUtil, GoogleTranslateClient googleTranslateClient,
		PlaceEsService placeEsService) {
		super(apiKeyConfig, tourApiClient, categoryService, groqApiClientUtil, om, googleTranslateClient,
			placeEsService, placeRepository);
		this.tourRepository = tourRepository;
	}

	@Override
	public TourismType getType() {
		return TourismType.TOURIST_SPOT;
	}

	@Override
	public void startPlaceUpdate(int numOfRow, int pageNo) {
		List<TourApiPlaceResponseDto> responseDtoList = requestPlace(numOfRow, pageNo);

		if (responseDtoList.isEmpty())
			return;
		for (TourApiPlaceResponseDto responseDto : responseDtoList) {
			checkAndUpdatePlace(responseDto);
		}
	}

	@Override
	public Optional<PlaceDetailResponseDto> getPlaceDetail(int placeId, com.tripsok_back.type.LocaleCode locale) {
		Optional<Place> optPlace = tourRepository.findById(placeId);
		if (optPlace.isEmpty())
			throw new TourApiException(InternalErrorCode.PLACE_DETAIL_NOT_FOUND);
		Place placeTour = optPlace.get();

		if (placeTour.getPlaceTr(LocaleCode.KO) == null ||
			!StringUtils.hasText(placeTour.getPlaceTr(LocaleCode.KO).getSummary())) {
			createShortDescription(placeTour);
		}
		if (locale != null && locale != LocaleCode.KO) {
			if (placeTour.getPlaceTr(locale) == null ||
				!StringUtils.hasText(placeTour.getPlaceTr(locale).getSummary())) {
				placeTour.upsertTranslation(locale, null, null, null, null);
				createSummaryTranslation(placeTour, locale);
			}
			createInformationTranslation(placeTour, locale);
			createTransliterationForNameAndAddress(placeTour, locale);
		}
		if (placeTour.getTour().getPlaceLclsCategory() == null ||
			(placeTour.getTour().getTourImages() == null || placeTour.getTour().getTourImages().isEmpty()) || placeTour.getTourismType() == null) {
			TourApiPlaceDetailResponseDto tourApiPlaceDetailResponseDto = requestPlaceDetail(
				placeTour.getContentId());
			PlaceLclsCategory category = categoryService.getCategoryByCode(
				tourApiPlaceDetailResponseDto.getCategoryLevel3());
			placeTour.updateNullTourDetail(tourApiPlaceDetailResponseDto, category);
		}

		ensurePlaceIntro(placeTour);
		addView(placeTour);
		tourRepository.save(placeTour);
		return Optional.of(PlaceDetailResponseDto.from(placeTour, PlaceJoinType.TOUR, locale));
	}

	@Override
	public void addView(Place place) {
		place.incrementViewCount();
	}

	@Override
	public void addLike(Place place) {
		place.incrementLikeCount();
	}

	@Override
	public PageResponse<PlaceBriefSlimResponseDto> getPlaceList(Pageable pageable,
		com.tripsok_back.type.LocaleCode locale) {
		Page<Place> placeList = tourRepository.findByTourIsNotNullAndPlaceTrs_Id_Locale(locale.getCode(), pageable);
		if (placeList.getTotalPages() == 0)
			return PageResponse.empty();
		Page<PlaceBriefSlimResponseDto> dtoList = placeList.map(
			e -> PlaceBriefSlimResponseDto.from(e, getType().name(),
				e.getTour().getImageUrlList().getFirst(),
				e.getTour().getTourImages().size(),
				e.getTour().getTourReviews().size(),
				locale)
		);
		return PageResponse.fromPage(placeList, dtoList);
	}

	@Override
	public PageResponse<PlaceBriefSlimResponseDto> getPlaceListByTheme(Pageable pageable, Integer themeId,
		LocaleCode locale) {
		Page<Place> placeList = tourRepository.findByTourIsNotNullAndThemes_Theme_Id(pageable, themeId);
		Page<PlaceBriefSlimResponseDto> dtoList = placeList.map(
			e -> PlaceBriefSlimResponseDto.from(e, getType().name(),
				e.getTour().getImageUrlList().getFirst(),
				e.getTour().getTourImages().size(),
				e.getTour().getTourReviews().size(),
				locale));
		return PageResponse.fromPage(placeList, dtoList);
	}

	@Override
	public void addReview(Integer userId, ReviewRequestDto reviewRequestdto) {

	}

	@Override
	public Page<Place> findAll(PageRequest of) {
		return tourRepository.findAllByTourIsNotNull(of);
	}

	public List<TourApiPlaceResponseDto> requestPlace(int numOfRow, int pageNo) {
		TourApiPlaceRequestDto TourRequestDto = TourApiPlaceRequestDto.builder()
			.numOfRows(numOfRow)
			.pageNo(pageNo)
			.arrange("R")
			.areaCode("32")
			.contentTypeId(this.getType().getId())
			.serviceKey(apiKeyConfig.getTourApiKey())
			.build();
		List<TourApiPlaceResponseDto> responseDtoList = tourApiClient.fetchPlaceData(TourRequestDto);
		log.info("{}개 응답 성공 (미리보기): {}", responseDtoList.size(), responseDtoList.getFirst());
		return responseDtoList;
	}

	public TourApiPlaceDetailResponseDto requestPlaceDetail(Integer contentId) {
		TourApiPlaceDetailRequestDto TourRequestDto = TourApiPlaceDetailRequestDto.builder()
			.contentId(contentId)
			.serviceKey(apiKeyConfig.getTourApiKey())
			.build();

		return tourApiClient.fetchPlaceDataDetail(TourRequestDto);
	}

	public Boolean checkAndUpdatePlace(TourApiPlaceResponseDto placeDto) {
		LocalDateTime placeUpdatedAt = TimeUtil.stringToLocalDateTime(placeDto.getModifiedTime());
		Optional<Place> place = tourRepository.findByContentId(placeDto.getContentId());
		if (place.isEmpty()) {
			addPlace(placeDto);
			log.info("숙소: ContentId:{} 신규 항목으로 추가", placeDto.getContentId());
			return true;
		}
		Place placeData = place.get();
		if (placeData.getUpdatedAt().equals(placeUpdatedAt)) {
			log.info("숙소: ContentId:{} 변경사항 없음", placeDto.getContentId());
			return false;
		} else {
			updatePlace(placeData, placeDto);
			log.info("숙소: ContentId:{} 변경사항으로 업데이트 진행", placeDto.getContentId());
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
		if (!org.springframework.util.StringUtils.hasText(source)) {
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
		if (place.getPlaceTr(LocaleCode.KO) == null)
			return;
		if (place.getPlaceTr(locale) != null &&
			org.springframework.util.StringUtils.hasText(place.getPlaceTr(locale).getSummary())) {
			return;
		}
		String name = place.getPlaceTr(LocaleCode.KO).getPlaceName();
		String base = place.getPlaceTr(LocaleCode.KO).getSummary();
		log.info("createTranslation: placeId={}, contentId={}, locale={}, name='{}'",
			place.getId(), place.getContentId(), locale != null ? locale.getCode() : null, name);
		if (!org.springframework.util.StringUtils.hasText(base)) {
			log.info("createTranslation: KO summary missing/empty, skip (placeId={}, locale={})",
				place.getId(), locale != null ? locale.getCode() : null);
			return;
		}
		String translated = googleTranslateClient.requestTranslation(base, locale);
		translated = groqApiClientUtil.sanitizeForVarchar(translated, 255);
		if (place.getPlaceTr(locale) == null) {
			place.upsertTranslation(locale, null, null, null, null);
		}
		place.getPlaceTr(locale).setSummary(translated);
	}

	private void createInformationTranslation(Place place, LocaleCode locale) {
		if (place.getPlaceTr(LocaleCode.KO) == null)
			return;
		String info = place.getPlaceTr(LocaleCode.KO).getInformation();
		if (!org.springframework.util.StringUtils.hasText(info))
			return;
		if (place.getPlaceTr(locale) == null) {
			place.upsertTranslation(locale, null, null, null, null);
		}
		if (org.springframework.util.StringUtils.hasText(place.getPlaceTr(locale).getInformation()))
			return;
		String translated = googleTranslateClient.requestTranslation(info, locale);
		place.getPlaceTr(locale).setInformation(translated);
	}

	private void createTransliterationForNameAndAddress(Place place, LocaleCode locale) {
		if (place.getPlaceTr(LocaleCode.KO) == null)
			return;
		if (locale == LocaleCode.KO)
			return;
		String koName = place.getPlaceTr(LocaleCode.KO).getPlaceName();
		String koAddr = place.getPlaceTr(LocaleCode.KO).getAddress();
		if (place.getPlaceTr(locale) == null) {
			place.upsertTranslation(locale, null, null, null, null);
		}
		if (org.springframework.util.StringUtils.hasText(koName)
			&& !org.springframework.util.StringUtils.hasText(place.getPlaceTr(locale).getPlaceName())) {
			String namePhon = googleTranslateClient.requestTransliteration(koName, locale);
			namePhon = groqApiClientUtil.sanitizeForVarchar(namePhon, 255);
			place.getPlaceTr(locale).setPlaceName(namePhon);
		}
		if (org.springframework.util.StringUtils.hasText(koAddr)
			&& !org.springframework.util.StringUtils.hasText(place.getPlaceTr(locale).getAddress())) {
			String addrPhon = googleTranslateClient.requestTransliteration(koAddr, locale);
			addrPhon = groqApiClientUtil.sanitizeForVarchar(addrPhon, 255);
			place.getPlaceTr(locale).setAddress(addrPhon);
		}
	}

	public void updatePlace(Place existingPlace, TourApiPlaceResponseDto placeDto) {
		TourApiPlaceDetailResponseDto detailResponseDto = requestPlaceDetail(existingPlace.getContentId());
		log.info("updatePlace: 상세정보 응답 성공 (미리보기):  (pretty)\n{}",
			JsonMapperUtil.pretty(om, detailResponseDto));
		PlaceLclsCategory category = categoryService.getCategoryByCode(
			detailResponseDto.getLargeClassificationSystem3());
		existingPlace.updateTour(placeDto, detailResponseDto, category);
		if (existingPlace.getTour() != null && existingPlace.getTour().getTourImages() == null) {
			existingPlace.updateNullTourDetail(detailResponseDto, category);
		}
		for (LocaleCode lc : LocaleCode.values()) {
			if (lc == LocaleCode.KO)
				continue;
			createSummaryTranslation(existingPlace, lc);
			createInformationTranslation(existingPlace, lc);
			createTransliterationForNameAndAddress(existingPlace, lc);
		}
		tourRepository.save(existingPlace);
		log.info("상세정보 업데이트 완료 : contentId={}, placeId={}, title={}, categoryName={}",
			existingPlace.getContentId(),
			existingPlace.getId(),
			existingPlace.getTour() != null ? existingPlace.getTour().getId() : null,
			existingPlace.getTour() != null ? existingPlace.getTour().getPlaceLclsCategory() : null
		);
	}

	public void addPlace(TourApiPlaceResponseDto placeDto) {
		TourApiPlaceDetailResponseDto detailResponseDto = requestPlaceDetail(placeDto.getContentId());
		log.info("addPlace: 상세정보 응답 성공 (미리보기): (pretty)\n{}",
			JsonMapperUtil.pretty(om, detailResponseDto));
		PlaceLclsCategory category = categoryService.getCategoryByCode(
			detailResponseDto.getLargeClassificationSystem3());
		Place tourPlace = Place.buildTour(placeDto, detailResponseDto, category);
		createShortDescription(tourPlace);
		for (LocaleCode localeCode : LocaleCode.values()) {
			if (localeCode.equals(LocaleCode.KO))
				continue;
			createSummaryTranslation(tourPlace, localeCode);
			createInformationTranslation(tourPlace, localeCode);
			createTransliterationForNameAndAddress(tourPlace, localeCode);
		}
		tourRepository.save(tourPlace);
	}

	@Override
	public int reindexFullEs() {
		int page = 0;
		int size = 500;
		int placeCount = 0;
		int docCount = 0;
		Page<Place> placePage;
		do {
			placePage = tourRepository.findAllByTourIsNotNullOrderByIdAsc(PageRequest.of(page, size));
			for (Place e : placePage.getContent()) {
				docCount += placeEsService.indexPlaceDocuments(e);
				placeCount++;
			}
			page++;
		} while (!placePage.isEmpty());
		log.info("TourFullIndex 완료 (places={}, docs={})", placeCount, docCount);
		return docCount;
	}
}
