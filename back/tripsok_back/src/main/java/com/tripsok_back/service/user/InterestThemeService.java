package com.tripsok_back.service.user;

import java.util.List;
import java.util.Set;

import com.tripsok_back.dto.user.response.InterestThemeResponse;
import com.tripsok_back.model.user.TripSokUser;
import com.tripsok_back.type.LocaleCode;

public interface InterestThemeService {
	List<InterestThemeResponse> getInterestThemes(TripSokUser user, LocaleCode locale);

	void updateInterestThemes(TripSokUser user, Set<Integer> interestThemeIds);
}
