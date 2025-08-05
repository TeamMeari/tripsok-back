package com.tripsok_back.batch.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tripsok_back.batch.api.TouristApiClient;
import com.tripsok_back.batch.domain.TourismType;
import com.tripsok_back.batch.repository.TourRepository;
import com.tripsok_back.config.ApiKeyConfig;
import com.tripsok_back.dto.TourApiPlaceDetailResponseDto;
import com.tripsok_back.dto.TourApiPlaceRequestDto;
import com.tripsok_back.dto.TourApiPlaceResponseDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class TourServiceImpl implements PlaceService {

	private final ApiKeyConfig apiKeyConfig;
	private final TouristApiClient tourApiClient;
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

	public TourApiPlaceDetailResponseDto requestPlaceDetail(Integer contentId) {
		return null;
	}

	public Boolean checkAndUpdatePlace(TourApiPlaceResponseDto placeDto) {
		return null;
	}

	public void updatePlace() {
		return;
	}

	public void addPlace() {
		return;
	}
}
