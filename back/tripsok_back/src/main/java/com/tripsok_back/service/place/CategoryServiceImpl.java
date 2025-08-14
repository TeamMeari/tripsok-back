package com.tripsok_back.service.place;

import java.util.List;

import org.springframework.stereotype.Service;

import com.tripsok_back.config.ApiKeyConfig;
import com.tripsok_back.dto.tourApi.LclsCategoryItemResponseDto;
import com.tripsok_back.dto.tourApi.LclsSystmCodeRequestDto;
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
		List<LclsCategoryItemResponseDto> dto = touristApiClientUtil.fetchCategories(
			LclsSystmCodeRequestDto.builder().serviceKey(apiKeyConfig.getTourApiKey()).build());
		Integer categoryCount = Math.toIntExact(lclsCategoryRepository.count());
		if (categoryCount > dto.size())
			return;

	}
}
