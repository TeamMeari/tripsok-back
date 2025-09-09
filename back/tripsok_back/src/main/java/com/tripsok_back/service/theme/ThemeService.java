package com.tripsok_back.service.theme;

import java.util.List;

import com.tripsok_back.dto.theme.response.ThemeResponse;

public interface ThemeService {
	List<ThemeResponse> getThemeList();
}
