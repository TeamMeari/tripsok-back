package com.tripsok_back.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tripsok_back.dto.theme.response.ThemeResponse;
import com.tripsok_back.service.theme.ThemeService;
import com.tripsok_back.type.LocaleCode;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/theme")
@RequiredArgsConstructor
@Slf4j
public class ThemeController {
	private final ThemeService themeService;

	@GetMapping
	public ResponseEntity<List<ThemeResponse>> getThemeList(
		@Parameter(description = "언어(로케일) 코드", example = "KO", schema = @Schema(type = "string", allowableValues = {
			"KO", "EN", "JA", "CN"}))
		@RequestParam(name = "locale", defaultValue = "KO") LocaleCode locale
	) {
		return ResponseEntity.ok().body(themeService.getThemeList(locale));
	}
}
