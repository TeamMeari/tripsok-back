package com.tripsok_back.service.place;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.tripsok_back.config.ApiKeyConfig;
import com.tripsok_back.dto.tourApi.LclsCategoryItemResponseDto;
import com.tripsok_back.dto.tourApi.LclsSystmCodeRequestDto;
import com.tripsok_back.exception.CustomInternalException;
import com.tripsok_back.exception.InternalErrorCode;
import com.tripsok_back.model.place.PlaceLclsCategory;
import com.tripsok_back.model.place.PlaceLclsCategoryTr;
import com.tripsok_back.repository.place.LclsCategoryRepository;
import com.tripsok_back.type.LocaleCode;
import com.tripsok_back.util.TouristApiClientUtil;

import jakarta.transaction.Transactional;
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
	@Transactional
	public void requestAndUpdateCategory() {
		Map<String, LclsCategoryItemResponseDto> koMap =
			touristApiClientUtil.fetchCategories(
				LclsSystmCodeRequestDto.builder().serviceKey(apiKeyConfig.getTourApiKey()).build(),
				LocaleCode.KO
			);

		List<PlaceLclsCategory> existing = lclsCategoryRepository.findAll();
		Set<String> existingCodes = existing.stream()
			.map(PlaceLclsCategory::getLclsSystm3Code)
			.filter(Objects::nonNull)
			.collect(Collectors.toSet());

		List<PlaceLclsCategory> toInsert = new ArrayList<>();
		Map<String, PlaceLclsCategory> newByCode = new HashMap<>();

		for (Map.Entry<String, LclsCategoryItemResponseDto> e : koMap.entrySet()) {
			String l3code = e.getKey();
			if (l3code == null || existingCodes.contains(l3code))
				continue;

			LclsCategoryItemResponseDto dto = e.getValue();

			PlaceLclsCategory entity = PlaceLclsCategory.fromDto(dto);
			PlaceLclsCategoryTr trKo = PlaceLclsCategoryTr.ofCascade(
				entity, LocaleCode.KO, dto.getLclsSystm1Nm(), dto.getLclsSystm2Nm(), dto.getLclsSystm3Nm()
			);
			entity.getPlaceLclsCategoryTrs().add(trKo);

			toInsert.add(entity);
			newByCode.put(l3code, entity);
		}

		for (LocaleCode localeCode : LocaleCode.values()) {
			if (localeCode == LocaleCode.KO)
				continue;

			Map<String, LclsCategoryItemResponseDto> locMap =
				touristApiClientUtil.fetchCategories(
					LclsSystmCodeRequestDto.builder().serviceKey(apiKeyConfig.getTourApiKey()).build(),
					localeCode
				);

			for (Map.Entry<String, LclsCategoryItemResponseDto> e : locMap.entrySet()) {
				String l3code = e.getKey();
				if (l3code == null)
					continue;

				PlaceLclsCategory entity = newByCode.get(l3code);
				if (entity == null)
					continue;

				LclsCategoryItemResponseDto dto = e.getValue();
				PlaceLclsCategoryTr tr = PlaceLclsCategoryTr.ofCascade(
					entity, localeCode, dto.getLclsSystm1Nm(), dto.getLclsSystm2Nm(), dto.getLclsSystm3Nm()
				);
				entity.getPlaceLclsCategoryTrs().add(tr);
			}
		}

		lclsCategoryRepository.saveAll(toInsert);
		log.info("신규 카테고리 {}건 저장(모든 로케일 번역 포함).", toInsert.size());
	}

	@Override
	public PlaceLclsCategory getCategoryByCode(String code) {
		log.info("카테고리 코드 {} 조회 시작", code);
		PlaceLclsCategory category = lclsCategoryRepository.findByLclsSystm3Code(code)
			.orElseThrow(() -> new CustomInternalException(InternalErrorCode.CATEGORY_NOT_FOUND));
		return category;
	}
}
