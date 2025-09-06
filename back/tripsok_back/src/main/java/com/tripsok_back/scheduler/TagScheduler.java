package com.tripsok_back.scheduler;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.tripsok_back.dto.place.PlaceThemeAndTagResponse;
import com.tripsok_back.model.place.Place;
import com.tripsok_back.model.place.PlaceTag;
import com.tripsok_back.model.place.PlaceTheme;
import com.tripsok_back.model.place.Tag;
import com.tripsok_back.repository.PlaceTagRepository;
import com.tripsok_back.repository.PlaceThemeRepository;
import com.tripsok_back.repository.place.PlaceRepository;
import com.tripsok_back.repository.place.TagRepository;
import com.tripsok_back.repository.theme.ThemeRepository;
import com.tripsok_back.util.AiUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class TagScheduler {
	private final AiUtil aiUtil;
	private final PlaceRepository placeRepository;
	private final ThemeRepository themeRepository;
	private final TagRepository tagRepository;
	private final PlaceThemeRepository placeThemeRepository;
	private final PlaceTagRepository placeTagRepository;

	@Scheduled(fixedRateString = "PT48H", initialDelayString = "PT2H")
	void runBatchTagRequestApi() {
		log.info("Place Tag 업데이트 시작");
		int pageNum = 0;
		while (true) {
			Pageable pageable = PageRequest.of(pageNum, 50);
			log.info("pageNum={} 장소 태그 업데이트 시작", pageNum);
			List<Place> places = placeRepository.findByOrderByCreatedAtDesc(pageable).getContent();
			if (places.isEmpty()) {
				log.info("Place Tag 업데이트 종료 - 업데이트할 장소가 없습니다");
				return;
			}
			Set<Integer> themedPlaces = placeThemeRepository.findByPlaceIn(places).stream().map(
				item -> item.getPlace().getId()).collect(Collectors.toSet());
			Set<Integer> taggedPlaces = placeTagRepository.findByPlaceIn(places).stream().map(
				item -> item.getPlace().getId()).collect(Collectors.toSet());
			// 존재하지 않는 Place만 남김
			List<Place> placeList = places.stream()
				.filter(p -> !themedPlaces.contains(p.getId()) || !taggedPlaces.contains(
					p.getId())) // 둘 중 하나라도 없으면 밑에 로직을 탐
				.toList();

			if (placeList.isEmpty()) {
				if (pageNum == 0) {
					log.info("최신 장소까지 태그 완료, Place Tag 업데이트 종료");
					return;
				} else {
					log.info("pageNum={} 태그 완료", pageNum);
					pageNum++;
					continue;
				}
			}
			String placeToString = placeList.stream()
				.map(place -> place.getContentId() + " : " + place.getInformation())
				.collect(Collectors.joining("\n\n"));
			PlaceThemeAndTagResponse response = aiUtil.getThemeAndTag(placeToString);
			for (PlaceThemeAndTagResponse.Item item : response.getPlaces()) {
				placeList.stream()
					.filter(place -> place.getContentId().equals(item.getContentId()))
					.findFirst()
					.ifPresentOrElse(
						place -> {
							if (!placeThemeRepository.existsByPlace(place)) {
								place.setThemes(themeRepository.findByTypeIn(item.getTheme())
									.stream()
									.map(theme -> new PlaceTheme(place, theme))
									.collect(Collectors.toSet()));
							}
							if (!placeTagRepository.existsByPlace(place)) {
								place.setTags(item.getTag()
									.stream()
									.map(tagName ->
										new PlaceTag(place, tagRepository.findByName(tagName)
											.orElseGet(() -> tagRepository.save(new Tag(tagName)))))
									.collect(Collectors.toSet()));
							}
						}, () -> log.error("contentId={}에 해당하는 장소가 DB에 없습니다", item.getContentId())
					);
			}
			placeRepository.saveAll(placeList);
			pageNum++;
		}
	}
}
