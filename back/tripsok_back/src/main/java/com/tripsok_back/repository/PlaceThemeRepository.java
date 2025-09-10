package com.tripsok_back.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tripsok_back.model.place.Place;
import com.tripsok_back.model.place.PlaceTheme;

@Repository
public interface PlaceThemeRepository extends JpaRepository<PlaceTheme, Integer> {
	List<PlaceTheme> findByPlaceIn(List<Place> places);

	boolean existsByPlace(Place place);
}
