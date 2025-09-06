package com.tripsok_back.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tripsok_back.model.place.Place;
import com.tripsok_back.model.place.PlaceTag;

@Repository
public interface PlaceTagRepository extends JpaRepository<PlaceTag, Integer> {
	List<PlaceTag> findByPlaceIn(List<Place> places);

	boolean existsByPlace(Place place);
}
