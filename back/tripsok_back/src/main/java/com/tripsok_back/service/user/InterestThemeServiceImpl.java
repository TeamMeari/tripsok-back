package com.tripsok_back.service.user;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.tripsok_back.dto.user.response.InterestThemeResponse;
import com.tripsok_back.model.theme.Theme;
import com.tripsok_back.model.user.InterestTheme;
import com.tripsok_back.model.user.TripSokUser;
import com.tripsok_back.repository.theme.ThemeRepository;
import com.tripsok_back.repository.theme.ThemeTrRepository;
import com.tripsok_back.repository.user.InterestThemeRepository;
import com.tripsok_back.type.LocaleCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class InterestThemeServiceImpl implements InterestThemeService {

	private final ThemeRepository themeRepository;
	private final ThemeTrRepository themeTrRepository;
	private final InterestThemeRepository interestThemeRepository;

	@Override
	public List<InterestThemeResponse> getInterestThemes(TripSokUser user, LocaleCode locale) {
		List<Theme> themeList = interestThemeRepository.findByUser(user).stream().map(InterestTheme::getTheme).toList();
		return themeTrRepository.findAllByThemeInAndLocaleOrderByThemeIdAsc(themeList, locale).stream()
			.map(it -> new InterestThemeResponse(it.getTheme().getId(), it.getName())).toList();
	}

	@Override
	public void updateInterestThemes(TripSokUser user, Set<Integer> interestThemeIds) {
		List<Integer> existingInterestIds = interestThemeRepository.findByUser(user)
			.stream()
			.map(it -> it.getTheme().getId())
			.toList();
		List<Integer> themesToDeleteIds = existingInterestIds.stream()
			.filter(it -> !interestThemeIds.contains(it)).toList();

		if (!themesToDeleteIds.isEmpty()) {
			interestThemeRepository.deleteAllByUserAndThemeIdIn(user, themesToDeleteIds);
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
