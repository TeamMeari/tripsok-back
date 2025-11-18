package com.tripsok_back.scheduler;

import java.util.HashSet;
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
import com.tripsok_back.model.place.TagTr;
import com.tripsok_back.repository.place.PlaceRepository;
import com.tripsok_back.repository.place.PlaceTagRepository;
import com.tripsok_back.repository.place.PlaceThemeRepository;
import com.tripsok_back.repository.place.TagRepository;
import com.tripsok_back.repository.place.TagTrRepository;
import com.tripsok_back.repository.theme.ThemeRepository;
import com.tripsok_back.type.LocaleCode;
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
	private final TagTrRepository tagTrRepository;
	private final PlaceThemeRepository placeThemeRepository;
	private final PlaceTagRepository placeTagRepository;

	@Scheduled(cron = "0 0 5 */2 * *")
		// 2일마다 오전 5시에 실행
	void runBatchTagRequestApi() {
		log.info("Place Tag 업데이트 시작");
		int pageNum = 0;
		long count = placeRepository.count();
		long totalPages = (count / 10) + ((count % 10 == 0) ? 0 : 1);
		while (pageNum < totalPages) {
			Pageable pageable = PageRequest.of(pageNum, 10);
			Set<Place> placeList = getTargetPlaces(pageable);
			if (placeList.isEmpty()) {
				log.info("pageNum={} 태그 완료", pageNum);
				pageNum++;
				continue;
			}
			String placeToString = convertPlaceInfoToString(placeList);
			PlaceThemeAndTagResponse response = aiUtil.getThemeAndTag(placeToString);
			updatePlaceThemeAndTag(placeList, response);
			placeRepository.saveAll(placeList);
			pageNum++;
			log.info("pageNum={} 장소 태그 업데이트 완료", pageNum - 1);
		}
	}

	private Set<Place> getTargetPlaces(Pageable pageable) {
		log.info("pageNum={} 장소 태그 업데이트 시작", pageable.getPageNumber());
		List<Place> places = placeRepository.findByOrderByCreatedAtDesc(pageable).getContent();
		if (places.isEmpty()) {
			return Set.of();
		}
		return places.stream()
			.filter(place -> place.getThemes().isEmpty() || place.getTags().isEmpty())
			.collect(Collectors.toSet());
	}

	private String convertPlaceInfoToString(Set<Place> placeList) {
		return placeList.stream()
			.map(place -> place.getContentId() + " : " + place.getPlaceTr(LocaleCode.KO).getInformation())
			.collect(Collectors.joining("\n\n"));
	}

	private void updatePlaceThemeAndTag(Set<Place> placeList, PlaceThemeAndTagResponse response) {
		for (PlaceThemeAndTagResponse.PlaceDto item : response.getPlaces()) {
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
							place.setTags(item.getTag().stream().map(tagDto -> {
									Tag tag = tagRepository.findByName(tagDto.getKo())
										.orElseGet(() -> savePlaceTr(tagDto));
									return new PlaceTag(place, tag);
								})
								.collect(Collectors.toSet()));
						}
					}, () -> log.error("contentId={}에 해당하는 장소가 DB에 없습니다", item.getContentId())
				);
		}
	}

	private Tag savePlaceTr(PlaceThemeAndTagResponse.TagDto tags) {
		Tag tag = tagRepository.save(new Tag(tags.getKo()));
		Set<TagTr> newTagTrs = new HashSet<>();
		for (LocaleCode locale : LocaleCode.values()) {
			if (locale == LocaleCode.KO)
				continue;
			String nameByLocale = tags.getNameByLocale(locale);
			if (nameByLocale != null && !nameByLocale.isBlank()) {
				newTagTrs.add(new TagTr(tag, locale, nameByLocale));
			}
		}
		tagTrRepository.saveAll(newTagTrs);
		return tag;
	}
}
