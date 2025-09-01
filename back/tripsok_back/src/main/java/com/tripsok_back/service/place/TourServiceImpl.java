package com.tripsok_back.service.place;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
import com.tripsok_back.repository.place.TourRepository;
import com.tripsok_back.type.PlaceJoinType;
import com.tripsok_back.type.TourismType;
import com.tripsok_back.util.JsonMapperUtil;
import com.tripsok_back.util.TimeUtil;
import com.tripsok_back.util.TouristApiClientUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class TourServiceImpl implements PlaceService {

	private final ApiKeyConfig apiKeyConfig;
	private final TouristApiClientUtil tourApiClient;
	private final TourRepository tourRepository;
	private final CategoryService categoryService;
	private final ObjectMapper om;

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
	public Optional<PlaceDetailResponseDto> getPlaceDetail(int placeId) {
		Optional<Place> optPlace = tourRepository.findById(placeId);
		if (optPlace.isEmpty())
			throw new TourApiException(InternalErrorCode.PLACE_DETAIL_NOT_FOUND);
		Place placeTour = optPlace.get();
		if (placeTour.getTour().getTourType().isEmpty()) {
			TourApiPlaceDetailResponseDto tourApiPlaceDetailResponseDto = requestPlaceDetail(
				placeTour.getContentId());
			String categoryName = categoryService.getCategoryByCode(tourApiPlaceDetailResponseDto.getCategoryLevel3());
			placeTour.updateNullTourDetail(tourApiPlaceDetailResponseDto, categoryName);
		}
		addView(placeTour);
		return Optional.of(PlaceDetailResponseDto.from(placeTour, PlaceJoinType.TOUR));
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
	public PageResponse<PlaceBriefResponseDto> getPlaceList(Pageable pageable) {
		Page<Place> placeList = tourRepository.findByTourIsNotNull(pageable);
		if (placeList.getTotalPages() == 0)
			return PageResponse.empty();
		Page<PlaceBriefResponseDto> dtoList = placeList.map(
			e -> PlaceBriefResponseDto.from(e, getType().name(),
				e.getTour().getImageUrlList().getFirst(),
				e.getTour().getTourImages().size(),
				e.getTour().getTourReviews().size()));
		return PageResponse.fromPage(placeList, dtoList);
	}

	@Override
	public void addReview(Integer userId, ReviewRequestDto reviewRequestdto) {

	}

	public List<TourApiPlaceResponseDto> requestPlace(int numOfRow, int pageNo) {
		TourApiPlaceRequestDto TourRequestDto = TourApiPlaceRequestDto.builder()
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
		List<TourApiPlaceResponseDto> responseDtoList = tourApiClient.fetchPlaceData(TourRequestDto);
		log.info("{}개 응답 성공 (미리보기): {}", responseDtoList.size(), responseDtoList.getFirst());
		return responseDtoList;
	}

	public TourApiPlaceDetailResponseDto requestPlaceDetail(Integer contentId) {
		TourApiPlaceDetailRequestDto TourRequestDto = TourApiPlaceDetailRequestDto.builder()
			.mobileOS("ETC")
			.mobileApp("tripsok-batch")
			.responseType("json")
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

	public void updatePlace(Place existingPlace, TourApiPlaceResponseDto placeDto) {
		TourApiPlaceDetailResponseDto detailResponseDto = requestPlaceDetail(existingPlace.getContentId());
		log.info("updatePlace: 상세정보 응답 성공 (미리보기):  (pretty)\n{}",
			JsonMapperUtil.pretty(om, detailResponseDto));
		existingPlace.updateTour(placeDto, detailResponseDto,
			categoryService.getCategoryByCode(detailResponseDto.getLargeClassificationSystem3()));
		tourRepository.save(existingPlace);
		log.info("상세정보 업데이트 완료 : contentId={}, placeId={}, title={}, categoryName={}",
			existingPlace.getContentId(),
			existingPlace.getId(),
			existingPlace.getTour() != null ? existingPlace.getTour().getId() : null,
			existingPlace.getTour() != null ? existingPlace.getTour().getTourType() : null
		);
	}

	public void addPlace(TourApiPlaceResponseDto placeDto) {
		TourApiPlaceDetailResponseDto detailResponseDto = requestPlaceDetail(placeDto.getContentId());
		log.info("addPlace: 상세정보 응답 성공 (미리보기): (pretty)\n{}",
			JsonMapperUtil.pretty(om, detailResponseDto));
		String categoryName = categoryService.getCategoryByCode(detailResponseDto.getLargeClassificationSystem3());
		Place tourPlace = Place.buildTour(placeDto, detailResponseDto, categoryName);
		tourRepository.save(tourPlace);
	}
}
