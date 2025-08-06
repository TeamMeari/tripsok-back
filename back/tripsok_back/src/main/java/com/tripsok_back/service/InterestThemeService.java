package com.tripsok_back.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.tripsok_back.model.Theme.Theme;
import com.tripsok_back.model.user.InterestTheme;
import com.tripsok_back.model.user.TripSokUser;
import com.tripsok_back.repository.InterestThemeRepository;
import com.tripsok_back.repository.ThemeRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class InterestThemeService {

	private final ThemeRepository themeRepository;
	private final InterestThemeRepository interestThemeRepository;
	public void saveInterestThemes(TripSokUser user, List<Integer> interestThemeIds) {
		if (interestThemeIds != null && !interestThemeIds.isEmpty()) {
			List<Theme> interestThemes = themeRepository.findAllById(interestThemeIds);
			List<InterestTheme> themesToSave = interestThemes.stream()
				.map(theme -> new InterestTheme(user, theme))
				.collect(Collectors.toList());
			interestThemeRepository.saveAll(themesToSave);
		}
	}
}
