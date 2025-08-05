package com.tripsok_back.batch.scheduler;

import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tripsok_back.batch.domain.TourismType;
import com.tripsok_back.batch.service.PlaceService;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class TouristApiScheduler {

	static int NUM_OF_ROW = 1;
	static int PAGE_NO = 1;
	private final List<PlaceService> placeService;

	private PlaceService getService(TourismType type) {
		return placeService.stream()
			.filter(p -> p.getType() == type)
			.findFirst()
			.orElseThrow(() -> new IllegalArgumentException("No processor found for type: " + type));
	}

	@Scheduled(cron = "0 0 1 * * *")
	@PostConstruct
	public void init() throws JsonProcessingException {
		runBatchAccommodationRequestApi();
		/*
		runBatchRestaurantRequestApi();
		runBatchTourRequestApi();
		 */
	}

	public void runBatchAccommodationRequestApi() throws JsonProcessingException {
		log.info("***속초 신규 숙소정보 요청 시작***");
		getService(TourismType.ACCOMMODATION).startPlaceUpdate(NUM_OF_ROW, PAGE_NO);
		//log.error("신규 관광정보 처리 실패");
	}

	public void runBatchRestaurantRequestApi() throws JsonProcessingException {
		log.info("***속초 신규 식당정보 요청 시작***");
		getService(TourismType.RESTAURANT).startPlaceUpdate(NUM_OF_ROW, PAGE_NO);
		//log.error("신규 관광정보 처리 실패");
	}

	public void runBatchTourRequestApi() throws JsonProcessingException {
		log.info("***속초 신규 투어정보 요청 시작***");
		getService(TourismType.TOURIST_SPOT).startPlaceUpdate(NUM_OF_ROW, PAGE_NO);
		//log.error("신규 관광정보 처리 실패");
	}
}
