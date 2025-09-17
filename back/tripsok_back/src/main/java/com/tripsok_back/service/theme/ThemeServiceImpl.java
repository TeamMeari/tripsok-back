package com.tripsok_back.service.theme;

import java.util.List;

import org.springframework.stereotype.Service;

import com.tripsok_back.dto.theme.response.ThemeResponse;
import com.tripsok_back.repository.theme.ThemeTrRepository;
import com.tripsok_back.type.LocaleCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ThemeServiceImpl implements ThemeService {
	private final ThemeTrRepository themeTrRepository;

	@Override
	public List<ThemeResponse> getThemeList(LocaleCode locale) {
		return themeTrRepository.findAllByLocaleOrderByThemeIdAsc(locale).stream().map(ThemeResponse::new).toList();
	}
}
