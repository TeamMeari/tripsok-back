package com.tripsok_back.batch;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.tripsok_back.dto.TouristItemRequestDto;
import com.tripsok_back.dto.TouristItemResponseDto;
import com.tripsok_back.type.TourismType;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class TouristApiScheduler {

	@Value("${TOUR_API_KEY}")
	private String tourApiKey;
	private final TouristApiClient touristApiClient;

	@PostConstruct
	public void init() {
		runBatchAccomodationRequestApi();
		runBatchRestaurantRequestApi();
		runBatchTourRequestApi();
	}

	@Scheduled(cron = "0 0 1 * * *")
	public void runBatchAccomodationRequestApi() {
		try {
			log.info("***속초 신규 숙소정보 요청 시작***");

			TouristItemRequestDto accomodationRequestDto = TouristItemRequestDto.builder()
				.numOfRows(2)
				.pageNo(1)
				.mobileOS("ETC")
				.mobileApp("tripsok-batch")
				.type("json")
				.arrange("R")
				.areaCode("32")
				.ContentTypeId(TourismType.ACCOMMODATION.getId())
				.serviceKey(tourApiKey)
				.build();
			log.info("서비스 api키: {}", accomodationRequestDto.getServiceKey());
			List<TouristItemResponseDto> responseDtoList = touristApiClient.fetchTouristData(accomodationRequestDto);
			log.info("{}개 응답 성공 (미리보기): {}", responseDtoList.size(), responseDtoList.get(0));

		} catch (Exception e) {
			log.error("신규 관광정보 처리 실패", e);
		}
	}

	@Scheduled(cron = "0 0 1 * * *")
	public void runBatchRestaurantRequestApi() {
		try {
			log.info("***속초 신규 식당정보 요청 시작***");

			TouristItemRequestDto accomodationRequestDto = TouristItemRequestDto.builder()
				.numOfRows(2)
				.pageNo(1)
				.mobileOS("ETC")
				.mobileApp("tripsok-batch")
				.type("json")
				.arrange("R")
				.areaCode("32")
				.ContentTypeId(TourismType.RESTAURANT.getId())
				.serviceKey(tourApiKey)
				.build();
			log.info("서비스 api키: {}", accomodationRequestDto.getServiceKey());
			List<TouristItemResponseDto> responseDtoList = touristApiClient.fetchTouristData(accomodationRequestDto);
			log.info("{}개 응답 성공 (미리보기): {}", responseDtoList.size(), responseDtoList.get(0));

		} catch (Exception e) {
			log.error("신규 관광정보 처리 실패", e);
		}
	}

	@Scheduled(cron = "0 0 1 * * *")
	public void runBatchTourRequestApi() {
		try {
			log.info("***속초 신규 투어정보 요청 시작***");

			TouristItemRequestDto accomodationRequestDto = TouristItemRequestDto.builder()
				.numOfRows(2)
				.pageNo(1)
				.mobileOS("ETC")
				.mobileApp("tripsok-batch")
				.type("json")
				.arrange("R")
				.areaCode("32")
				.ContentTypeId(TourismType.TOURIST_SPOT.getId())
				.serviceKey(tourApiKey)
				.build();
			log.info("서비스 api키: {}", accomodationRequestDto.getServiceKey());
			List<TouristItemResponseDto> responseDtoList = touristApiClient.fetchTouristData(accomodationRequestDto);
			log.info("{}개 응답 성공 (미리보기): {}", responseDtoList.size(), responseDtoList.get(0));

		} catch (Exception e) {
			log.error("신규 관광정보 처리 실패", e);
		}
	}
}
