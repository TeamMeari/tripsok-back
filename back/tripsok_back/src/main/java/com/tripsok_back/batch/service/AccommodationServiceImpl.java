package com.tripsok_back.batch.service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tripsok_back.batch.api.TouristApiClient;
import com.tripsok_back.batch.domain.TourismType;
import com.tripsok_back.batch.repository.AccommodationRepository;
import com.tripsok_back.config.ApiKeyConfig;
import com.tripsok_back.dto.TourApiPlaceDetailRequestDto;
import com.tripsok_back.dto.TourApiPlaceDetailResponseDto;
import com.tripsok_back.dto.TourApiPlaceRequestDto;
import com.tripsok_back.dto.TourApiPlaceResponseDto;
import com.tripsok_back.model.place.Place;
import com.tripsok_back.util.TimeUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccommodationServiceImpl implements PlaceService {

	private final ApiKeyConfig apiKeyConfig;
	private final TouristApiClient tourApiClient;
	private final AccommodationRepository accommodationRepository;

	@Override
	public TourismType getType() {
		return TourismType.ACCOMMODATION;
	}

	@Override
	public void startPlaceUpdate(int numOfRow, int pageNo) throws JsonProcessingException {

		List<TourApiPlaceResponseDto> responseDtoList = requestPlace(numOfRow, pageNo);

		if (responseDtoList.isEmpty()) {
			log.info("응답받은 API 값이 없습니다");
			return;
		}
		for (TourApiPlaceResponseDto responseDto : responseDtoList) {
			if (checkAndUpdatePlace(responseDto)) {
				//
			}
		}
	}

	public List<TourApiPlaceResponseDto> requestPlace(int numOfRow, int pageNo) throws JsonProcessingException {
		TourApiPlaceRequestDto accommodationRequestDto = TourApiPlaceRequestDto.builder()
			.numOfRows(numOfRow)
			.pageNo(pageNo)
			.mobileOS("ETC")
			.mobileApp("tripsok-batch")
			.type("json")
			.arrange("R")
			.areaCode("32")
			.ContentTypeId(this.getType().getId())
			.serviceKey(apiKeyConfig.getTourApiKey())
			.build();
		List<TourApiPlaceResponseDto> responseDtoList = tourApiClient.fetchPlaceData(accommodationRequestDto);
		log.info("{}개 응답 성공 (미리보기): {}", responseDtoList.size(), responseDtoList.getFirst());
		return responseDtoList;
	}

	public Optional<TourApiPlaceDetailResponseDto> requestPlaceDetail(Integer contentId) throws
		JsonProcessingException {
		TourApiPlaceDetailRequestDto accommodationRequestDto = TourApiPlaceDetailRequestDto.builder()
			.mobileOS("ETC")
			.mobileApp("tripsok-batch")
			.responseType("json")
			.contentId(contentId)
			.serviceKey(apiKeyConfig.getTourApiKey())
			.build();
		Optional<TourApiPlaceDetailResponseDto> responseDto = Optional.ofNullable(tourApiClient.fetchPlaceDataDetail(
			accommodationRequestDto));

		return responseDto;
	}

	public Boolean checkAndUpdatePlace(TourApiPlaceResponseDto placeDto) throws JsonProcessingException {
		Instant placeUpdatedAt = Instant.from(TimeUtil.StringToInstant(placeDto.getModifiedTime()));
		Optional<Place> place = accommodationRepository.findByContentId(placeDto.getContentId());
		if (!place.isPresent()) {
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
			existingPlace.updateAccommodation(placeDto, detailResponseDto.get());
			accommodationRepository.save(existingPlace);
		});
	}

	public void addPlace(TourApiPlaceResponseDto placeDto) throws JsonProcessingException {
		Optional<TourApiPlaceDetailResponseDto> detailResponseDto = requestPlaceDetail(placeDto.getContentId());
		detailResponseDto.ifPresent(e -> {
			log.info("상세정보 응답 성공 (미리보기): {}", detailResponseDto.get());
			Place accomodationPlace = Place.buildAccommodation(placeDto, detailResponseDto.get());
			accommodationRepository.save(accomodationPlace);
		});
	}
}
