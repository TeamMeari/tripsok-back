package com.tripsok_back.service.user;

import java.util.List;
import java.util.Set;

import com.tripsok_back.dto.user.response.InterestThemeResponse;
import com.tripsok_back.model.user.TripSokUser;

public interface InterestThemeService {
	void saveInterestThemes(TripSokUser user, List<Integer> interestThemeIds);
	List<InterestThemeResponse> getInterestThemes(TripSokUser user);
	void updateInterestThemes(TripSokUser user, Set<Integer> interestThemeIds);
}
