package com.tripsok_back.dto.theme.response;

import com.tripsok_back.model.theme.ThemeTr;

public record ThemeResponse(Integer id, String type) {
	public ThemeResponse(ThemeTr themeTr) {
		this(themeTr.getTheme().getId(), themeTr.getName());
	}
}
