package com.tripsok_back.service.place;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tripsok_back.config.ApiKeyConfig;
import com.tripsok_back.dto.tourApi.TourApiPlaceDetailRequestDto;
import com.tripsok_back.dto.tourApi.TourApiPlaceDetailResponseDto;
import com.tripsok_back.dto.tourApi.TourApiPlaceRequestDto;
import com.tripsok_back.dto.tourApi.TourApiPlaceResponseDto;
import com.tripsok_back.model.place.Place;
import com.tripsok_back.repository.place.TourRepository;
import com.tripsok_back.type.TourismType;
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

	@Override
	public TourismType getType() {
		return TourismType.TOURIST_SPOT;
	}

	@Override
	public void startPlaceUpdate(int numOfRow, int pageNo) throws JsonProcessingException {
		List<TourApiPlaceResponseDto> responseDtoList = requestPlace(numOfRow, pageNo);

		if (responseDtoList.isEmpty())
			return;
		for (TourApiPlaceResponseDto responseDto : responseDtoList) {
			checkAndUpdatePlace(responseDto);
		}
	}

	public List<TourApiPlaceResponseDto> requestPlace(int numOfRow, int pageNo) throws JsonProcessingException {
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

	public Optional<TourApiPlaceDetailResponseDto> requestPlaceDetail(Integer contentId) throws
		JsonProcessingException {
		TourApiPlaceDetailRequestDto TourRequestDto = TourApiPlaceDetailRequestDto.builder()
			.mobileOS("ETC")
			.mobileApp("tripsok-batch")
			.responseType("json")
			.contentId(contentId)
			.serviceKey(apiKeyConfig.getTourApiKey())
			.build();

		return Optional.ofNullable(tourApiClient.fetchPlaceDataDetail(
			TourRequestDto));
	}

	public Boolean checkAndUpdatePlace(TourApiPlaceResponseDto placeDto) throws JsonProcessingException {
		LocalDateTime placeUpdatedAt = LocalDateTime.from(TimeUtil.stringToLocalDateTime(placeDto.getModifiedTime()));
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

	public void updatePlace(Place existingPlace, TourApiPlaceResponseDto placeDto) throws JsonProcessingException {
		Optional<TourApiPlaceDetailResponseDto> detailResponseDto = requestPlaceDetail(existingPlace.getContentId());
		detailResponseDto.ifPresent(e -> {
			log.info("상세정보 응답 성공 (미리보기): {}", detailResponseDto.get());
			existingPlace.updateTour(placeDto, detailResponseDto.get());
			tourRepository.save(existingPlace);
		});
	}

	public void addPlace(TourApiPlaceResponseDto placeDto) throws JsonProcessingException {
		Optional<TourApiPlaceDetailResponseDto> detailResponseDto = requestPlaceDetail(placeDto.getContentId());
		detailResponseDto.ifPresent(e -> {
			log.info("상세정보 응답 성공 (미리보기): {}", detailResponseDto.get());
			Place tourPlace = Place.buildTour(placeDto, detailResponseDto.get());
			tourRepository.save(tourPlace);
		});
	}
}
