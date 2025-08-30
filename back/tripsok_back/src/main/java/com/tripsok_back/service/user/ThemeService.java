package com.tripsok_back.service.user;

import java.util.List;

import com.tripsok_back.dto.user.response.ThemeResponse;

public interface ThemeService {
	List<ThemeResponse> getThemeList();
}
