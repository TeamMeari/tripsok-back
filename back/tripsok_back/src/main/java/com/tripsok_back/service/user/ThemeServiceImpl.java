package com.tripsok_back.service.user;

import java.util.List;

import org.springframework.stereotype.Service;

import com.tripsok_back.dto.user.response.ThemeResponse;
import com.tripsok_back.repository.ThemeRepository;

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
