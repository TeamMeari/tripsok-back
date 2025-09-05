package com.tripsok_back.scheduler;

import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.tripsok_back.repository.place.PlaceRepository;
import com.tripsok_back.util.AiUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class TagScheduler {
	private final AiUtil aiUtil;
	private final PlaceRepository placeRepository;

	@Scheduled(cron = "0 0 5 * * *")
	void runBatchTagRequestApi() {
		log.info("Place Tag 업데이트 시작");
		Pageable pageable = PageRequest.of(0, 10);
		String places = placeRepository.findTop10ByOrderByCreatedAtAsc(pageable).getContent().stream()
			.map(place -> String.valueOf(place.getId())) // 원하는 필드로 변경
			.collect(Collectors.joining(","));
		aiUtil.getThemeAndTag(places);
	}
}
