package com.tripsok_back.service.theme;

import java.util.List;

import org.springframework.stereotype.Service;

import com.tripsok_back.dto.theme.response.ThemeResponse;
import com.tripsok_back.repository.theme.ThemeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ThemeServiceImpl implements ThemeService {
	private final ThemeRepository themeRepository;

	@Override
	public List<ThemeResponse> getThemeList() {
		return themeRepository.findAllByOrderByIdAsc()
			.stream()
			.map(it -> new ThemeResponse(it.getId(), it.getType()))
			.toList();
	}
}
