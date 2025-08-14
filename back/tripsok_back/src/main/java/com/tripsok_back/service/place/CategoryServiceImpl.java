package com.tripsok_back.service.place;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.tripsok_back.config.ApiKeyConfig;
import com.tripsok_back.dto.tourApi.LclsCategoryItemResponseDto;
import com.tripsok_back.dto.tourApi.LclsSystmCodeRequestDto;
import com.tripsok_back.model.place.PlaceLclsCategory;
import com.tripsok_back.repository.place.LclsCategoryRepository;
import com.tripsok_back.util.TouristApiClientUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

	private final TouristApiClientUtil touristApiClientUtil;
	private final ApiKeyConfig apiKeyConfig;
	private final LclsCategoryRepository lclsCategoryRepository;

	@Override
	public void requestAndUpdateCategory() {
		Map<String, LclsCategoryItemResponseDto> dtoMap = touristApiClientUtil.fetchCategories(
			LclsSystmCodeRequestDto.builder().serviceKey(apiKeyConfig.getTourApiKey()).build());
		List<PlaceLclsCategory> existing = lclsCategoryRepository.findAll();
		Set<String> existingCodes = existing.stream()
			.map(PlaceLclsCategory::getLclsSystm3Code) // 엔티티 필드명에 맞게
			.filter(Objects::nonNull)
			.collect(Collectors.toSet());

		List<PlaceLclsCategory> toInsert = new ArrayList<>();
		for (Map.Entry<String, LclsCategoryItemResponseDto> responseDto : dtoMap.entrySet()) {
			String l3code = responseDto.getKey();
			if (l3code == null || existingCodes.contains(l3code))
				continue;

			LclsCategoryItemResponseDto dto = responseDto.getValue();

			PlaceLclsCategory entity = PlaceLclsCategory.fromDto(dto);
			toInsert.add(entity);
		}
		lclsCategoryRepository.saveAll(toInsert);
		log.info("신규 카테고리 {}건 저장", toInsert.size());

	}
}
