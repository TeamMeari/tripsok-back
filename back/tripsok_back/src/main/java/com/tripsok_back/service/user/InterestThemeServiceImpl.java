package com.tripsok_back.service.user;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.tripsok_back.dto.user.response.InterestThemeResponse;
import com.tripsok_back.model.theme.Theme;
import com.tripsok_back.model.user.InterestTheme;
import com.tripsok_back.model.user.TripSokUser;
import com.tripsok_back.repository.user.InterestThemeRepository;
import com.tripsok_back.repository.user.ThemeRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class InterestThemeServiceImpl implements InterestThemeService {

	private final ThemeRepository themeRepository;
	private final InterestThemeRepository interestThemeRepository;

	@Override
	public void saveInterestThemes(TripSokUser user, List<Integer> interestThemeIds) {
		if (interestThemeIds != null && !interestThemeIds.isEmpty()) {
			List<Theme> interestThemes = themeRepository.findAllById(interestThemeIds);
			List<InterestTheme> themesToSave = interestThemes.stream()
				.map(theme -> new InterestTheme(user, theme))
				.collect(Collectors.toList());
			interestThemeRepository.saveAll(themesToSave);
		}
	}

	@Override
	public List<InterestThemeResponse> getInterestThemes(TripSokUser user) {
		return interestThemeRepository.findByUser(user)
			.stream()
			.map(it -> new InterestThemeResponse(it.getTheme().getId(), it.getTheme().getType()))
			.toList();
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
