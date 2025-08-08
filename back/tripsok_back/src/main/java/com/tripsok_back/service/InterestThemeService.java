package com.tripsok_back.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.tripsok_back.dto.user.response.InterestThemeResponse;
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
			List<Theme> interestThemes = themeRepository.findAllByIdIn(interestThemeIds);
			List<InterestTheme> themesToSave = interestThemes.stream()
				.map(theme -> new InterestTheme(user, theme)).toList();
			interestThemeRepository.saveAll(themesToSave);
		}
	}

	public List<InterestThemeResponse> getInterestThemes(TripSokUser user) {
		return interestThemeRepository.findByUser(user).stream().map(it -> new InterestThemeResponse(it.getTheme().getId(), it.getTheme().getType())).toList();
	}

	public void updateInterestThemes(TripSokUser user, List<Integer> interestThemeIds) {
		List<Integer> existingInterestIds = interestThemeRepository.findByUser(user).stream().map(it -> it.getTheme().getId()).toList();
		List<Integer> themesToDeleteIds = existingInterestIds.stream()
			.filter(it -> !interestThemeIds.contains(it)).toList();

		if (!themesToDeleteIds.isEmpty()) {
			interestThemeRepository.deleteAllByThemeIdIn(themesToDeleteIds);
		}

		List<Integer> themeIdsToAddIds = interestThemeIds.stream()
			.filter(id -> !existingInterestIds.contains(id)).toList();

		if (!themeIdsToAddIds.isEmpty()) {
			List<Theme> themesToAdd = themeRepository.findAllByIdIn(themeIdsToAddIds);
			List<InterestTheme> newInterestThemes = themesToAdd.stream()
				.map(theme -> new InterestTheme(user, theme)).toList();
			interestThemeRepository.saveAll(newInterestThemes);
		}
	}
}
