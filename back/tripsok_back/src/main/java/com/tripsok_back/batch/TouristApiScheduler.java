package com.tripsok_back.batch;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.tripsok_back.dto.TouristItemRequestDto;
import com.tripsok_back.dto.TouristItemResponseDto;

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
		runBatchTouristApiRequest();
	}

	@Scheduled(cron = "0 0 1 * * *")
	public void runBatchTouristApiRequest() {
		try {
			log.info("속초 신규 관광정보 배치 요청 시작");

			TouristItemRequestDto dto = TouristItemRequestDto.builder()
				.numOfRows(2)
				.pageNo(1)
				.mobileOS("ETC")
				.mobileApp("tripsok-batch")
				.type("json")
				.arrange("R")
				.areaCode("32")
				.serviceKey(tourApiKey)
				.build();
			log.info("서비스 api키: {}", dto.getServiceKey());
			List<TouristItemResponseDto> responseDtoList = touristApiClient.fetchTouristData(dto);
			log.info("{}개 응답 성공 (미리보기): {}", responseDtoList.size(), responseDtoList.get(0));

		} catch (Exception e) {
			log.error("신규 관광정보 처리 실패", e);
		}
	}
}
