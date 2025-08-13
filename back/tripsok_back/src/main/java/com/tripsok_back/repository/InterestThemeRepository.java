package com.tripsok_back.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tripsok_back.model.user.InterestTheme;
import com.tripsok_back.model.user.TripSokUser;

public interface InterestThemeRepository extends JpaRepository<InterestTheme, Integer> {
	List<InterestTheme> findByUser(TripSokUser user);

	void deleteAllByUserAndThemeIdIn(TripSokUser user, List<Integer> themeIds);
}
