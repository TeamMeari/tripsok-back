package com.tripsok_back.service.theme;

import java.util.List;

import com.tripsok_back.dto.theme.response.ThemeResponse;
import com.tripsok_back.type.LocaleCode;

public interface ThemeService {
	List<ThemeResponse> getThemeList(LocaleCode locale);

	String getThemeType(Integer themeId);
}
