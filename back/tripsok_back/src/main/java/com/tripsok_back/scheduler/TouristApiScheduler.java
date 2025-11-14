package com.tripsok_back.scheduler;

import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.tripsok_back.exception.ServiceBlockException;
import com.tripsok_back.service.place.CategoryService;
import com.tripsok_back.service.place.PlaceService;
import com.tripsok_back.service.search.PlaceEsService;
import com.tripsok_back.type.TourismType;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class TouristApiScheduler {

	static int NUM_OF_ROW = 300;
	static int PAGE_NO = 1;
	private final List<PlaceService> placeService;
	private final CategoryService categoryService;
	private final PlaceEsService placeEsService;

	private PlaceService getService(TourismType type) {
		return placeService.stream()
			.filter(p -> p.getType() == type)
			.findFirst()
			.orElseThrow(() -> new IllegalArgumentException("No processor found for type: " + type));
	}

	@Scheduled(fixedRateString = "PT24H", initialDelayString = "PT30M") // 24시간마다 실행, 애플리케이션 시작 후 30분 후에 첫 실행
	public void initTourPlaceRequest() throws ServiceBlockException {

		try {
			runBatchCategoryRequestApi();
			runBatchAccommodationRequestApi();
			runBatchRestaurantRequestApi();
			runBatchTourRequestApi();
		} catch (ServiceBlockException e) {
			log.error(e.getMessage());
		}
		runFullEsIndexUpdate();
	}

	public void runBatchAccommodationRequestApi() throws ServiceBlockException {
		log.info("***속초 신규 숙소정보 요청 시작***");
		getService(TourismType.ACCOMMODATION).startPlaceUpdate(NUM_OF_ROW, PAGE_NO);
	}

	public void runBatchRestaurantRequestApi() throws ServiceBlockException {
		log.info("***속초 신규 식당정보 요청 시작***");
		getService(TourismType.RESTAURANT).startPlaceUpdate(NUM_OF_ROW, PAGE_NO);
	}

	public void runBatchTourRequestApi() throws ServiceBlockException {
		log.info("***속초 신규 투어정보 요청 시작***");
		getService(TourismType.TOURIST_SPOT).startPlaceUpdate(NUM_OF_ROW, PAGE_NO);
	}

	public void runBatchCategoryRequestApi() throws ServiceBlockException {
		log.info("***관광정보 카테고리 요청 시작***");
		categoryService.requestAndUpdateCategory();
	}

	public void runFullEsIndexUpdate() {
		placeEsService.createIndexIfMissing();
		if (placeEsService.checkIfReindexNeeds()) {
			log.info("재색인이 필요하지 않습니다");
			return;
		}

		log.info("*** ES Full Index(places) 재색인 시작 ***");
		int docs = 0;
		docs += getService(TourismType.ACCOMMODATION).reindexFullEs();
		docs += getService(TourismType.RESTAURANT).reindexFullEs();
		docs += getService(TourismType.TOURIST_SPOT).reindexFullEs();
		log.info("*** ES Full Index(places) 재색인 완료 (docs={}) ***", docs);
	}

}
