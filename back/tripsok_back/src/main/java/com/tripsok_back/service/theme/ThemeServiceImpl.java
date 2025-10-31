package com.tripsok_back.service.theme;

import java.util.List;

import org.springframework.stereotype.Service;

import com.tripsok_back.dto.theme.response.ThemeResponse;
import com.tripsok_back.model.theme.Theme;
import com.tripsok_back.repository.theme.ThemeRepository;
import com.tripsok_back.repository.theme.ThemeTrRepository;
import com.tripsok_back.type.LocaleCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ThemeServiceImpl implements ThemeService {
	private final ThemeTrRepository themeTrRepository;
	private final ThemeRepository themeRepository;

	@Override
	public List<ThemeResponse> getThemeList(LocaleCode locale) {
		return themeTrRepository.findAllByLocaleOrderByThemeIdAsc(locale).stream().map(ThemeResponse::new).toList();
	}

	@Override
	public String getThemeType(Integer themeId) {
		if (themeId == null) {
			return null;
		}
		String themeType = themeRepository.findById(themeId)
			.map(Theme::getType)
			.orElse(null);
		log.debug("조회된 테마 타입: {}", themeType);
		return themeType;
	}
}
