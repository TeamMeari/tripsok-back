package com.tripsok_back.service.place;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.util.InternalException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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
import com.tripsok_back.repository.place.accomodation.AccommodationRepository;
import com.tripsok_back.type.PlaceJoinType;
import com.tripsok_back.type.TourismType;
import com.tripsok_back.util.JsonMapperUtil;
import com.tripsok_back.util.TimeUtil;
import com.tripsok_back.util.TouristApiClientUtil;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccommodationServiceImpl implements PlaceService {

	private final ApiKeyConfig apiKeyConfig;
	private final TouristApiClientUtil tourApiClient;
	private final CategoryService categoryService;
	private final AccommodationRepository accommodationRepository;
	private final ObjectMapper om;

	@Override
	public TourismType getType() {
		return TourismType.ACCOMMODATION;
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
	public Optional<PlaceDetailResponseDto> getPlaceDetail(int placeId) throws TourApiException {
		Optional<Place> optPlace = accommodationRepository.findById(placeId);
		if (optPlace.isEmpty())
			throw new TourApiException(InternalErrorCode.PLACE_DETAIL_NOT_FOUND);
		Place placeAccommodation = optPlace.get();
		if (placeAccommodation.getAccommodation().getAccommodationType().isEmpty()) {
			TourApiPlaceDetailResponseDto tourApiPlaceDetailResponseDto = requestPlaceDetail(
				placeAccommodation.getContentId());
			String categoryName = categoryService.getCategoryByCode(tourApiPlaceDetailResponseDto.getCategoryLevel3());
			placeAccommodation.updateNullAccommodationDetail(tourApiPlaceDetailResponseDto, categoryName);
		}
		addView(placeAccommodation);
		return Optional.of(PlaceDetailResponseDto.from(placeAccommodation, PlaceJoinType.ACCOMMODATION));
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
	public PageResponse<PlaceBriefResponseDto> getPlaceList(Pageable pageable) throws TourApiException {
		Page<Place> placeList = accommodationRepository.findByAccommodationIsNotNull(pageable);
		return toPlaceBriefResponseDto(placeList);
	}

	public PageResponse<PlaceBriefResponseDto> getPlaceListByTheme(Pageable pageable, Integer themeId) {
		Page<Place> placeList = accommodationRepository.findByAccommodationIsNotNullAndThemes_Theme_Id(pageable, themeId);
		return toPlaceBriefResponseDto(placeList);
	}

	@Override
	public void addReview(Integer userId, ReviewRequestDto reviewRequestdto) {

	}

	public List<TourApiPlaceResponseDto> requestPlace(int numOfRow, int pageNo) {
		TourApiPlaceRequestDto accommodationRequestDto = TourApiPlaceRequestDto.builder()
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
		List<TourApiPlaceResponseDto> responseDtoList = tourApiClient.fetchPlaceData(accommodationRequestDto);
		if (!responseDtoList.isEmpty()) {
			log.info("RequestPlace: {}개 응답 성공 (미리보기): {}", responseDtoList.size(),
				JsonMapperUtil.pretty(om, responseDtoList.getFirst()));
		}
		return responseDtoList;
	}

	public TourApiPlaceDetailResponseDto requestPlaceDetail(Integer contentId) throws
		InternalException {
		TourApiPlaceDetailRequestDto accommodationRequestDto = TourApiPlaceDetailRequestDto.builder()
			.mobileOS("ETC")
			.mobileApp("tripsok-batch")
			.responseType("json")
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

	public void updatePlace(Place existingPlace, TourApiPlaceResponseDto placeDto) {
		TourApiPlaceDetailResponseDto detailResponseDto = requestPlaceDetail(existingPlace.getContentId());
		log.info("updatePlace: 상세정보 응답 성공 (미리보기):  (pretty)\n{}",
			JsonMapperUtil.pretty(om, detailResponseDto));
		existingPlace.updateAccommodation(placeDto, detailResponseDto,
			categoryService.getCategoryByCode(detailResponseDto.getLargeClassificationSystem3()));
		accommodationRepository.save(existingPlace);
		log.info("상세정보 업데이트 완료 : contentId={}, placeId={}, title={}, categoryName={}",
			existingPlace.getContentId(),
			existingPlace.getId(),
			existingPlace.getAccommodation() != null ? existingPlace.getAccommodation().getId() : null,
			existingPlace.getAccommodation() != null ? existingPlace.getAccommodation().getAccommodationType() : null
		);
	}

	public void addPlace(TourApiPlaceResponseDto placeDto) {
		TourApiPlaceDetailResponseDto detailResponseDto = requestPlaceDetail(placeDto.getContentId());
		log.info("addPlace: 상세정보 응답 성공 (미리보기): (pretty)\n{}",
			JsonMapperUtil.pretty(om, detailResponseDto));
		String categoryName = categoryService.getCategoryByCode(detailResponseDto.getLargeClassificationSystem3());
		Place accommodationPlace = Place.buildAccommodation(placeDto, detailResponseDto, categoryName);
		accommodationRepository.save(accommodationPlace);
	}

	private PageResponse<PlaceBriefResponseDto> toPlaceBriefResponseDto(Page<Place> placeList) {
		if (placeList.getTotalPages() == 0)
			return PageResponse.empty();
		Page<PlaceBriefResponseDto> dtoList = placeList.map(
			e -> PlaceBriefResponseDto.from(e, getType().name(),
				e.getAccommodation().getImageUrlList().getFirst(),
				e.getAccommodation().getAccommodationImages().size(),
				e.getAccommodation().getAccommodationReviews().size()));
		return PageResponse.fromPage(placeList, dtoList);
	}

}
