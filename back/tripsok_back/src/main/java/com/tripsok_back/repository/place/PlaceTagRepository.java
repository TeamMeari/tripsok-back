package com.tripsok_back.repository.place;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tripsok_back.model.place.Place;
import com.tripsok_back.model.place.PlaceTag;

@Repository
public interface PlaceTagRepository extends JpaRepository<PlaceTag, Integer> {
	boolean existsByPlace(Place place);
}
